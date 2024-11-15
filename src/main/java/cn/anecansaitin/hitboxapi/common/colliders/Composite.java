package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.CompositeRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;

public final class Composite implements ICollider {
    public final Vector3f position;
    public final Quaternionf rotation;
    private final HashMap<String, IntObjectPair<ICollider>> collisionMap = new HashMap<>();
    private final Pair<String, ICollider>[] collisionList;
    public final Vector3f globalPosition;
    public final Quaternionf globalRotation;

    public boolean disable;
    private boolean isDirty;

    @SuppressWarnings("unchecked")
    public Composite(Vector3f position, Quaternionf rotation, List<Pair<String, ICollider>> collisions) {
        this.position = position;
        this.globalPosition = new Vector3f(position);
        this.rotation = rotation;
        this.globalRotation = new Quaternionf(rotation);
        collisionList = collisions.toArray(new Pair[0]);

        for (Pair<String, ICollider> collision : collisionList) {
            if (collision.right().getType() == Collision.AABB) {
                throw new IllegalArgumentException("Composite cannot contain AABB collisions, because it can't rotate");
            }

            collisionMap.put(collision.left(), new IntObjectImmutablePair<>(collisionList.length, collision.right()));
        }
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        poseStack.push();

        if (isDirty || poseStack.isDirty()) {
            BoxPoseStack.Pose pose = poseStack.last();
            Vector3f posOffset = pose.position;
            Quaternionf rotOffset = pose.rotation;
            rotOffset.transform(this.position, globalPosition).add(posOffset);
            rotOffset.mul(this.rotation, globalRotation);
            poseStack.setDirty(true);
            isDirty = false;
        }

        poseStack.setPosition(globalPosition);
        poseStack.setRotation(globalRotation);

        for (Pair<String, ICollider> collision : collisionList) {
            collision.right().prepareColliding(poseStack);
        }

        poseStack.pop();
    }

    public void markDirty() {
        isDirty = true;
    }

    @Override
    public Collision getType() {
        return Collision.COMPOSITE;
    }

    @Override
    public ICollisionRender getRenderer() {
        return CompositeRender.INSTANCE;
    }

    public ICollider getCollision(String name) {
        return collisionMap.get(name).right();
    }

    public ICollider getCollision(int index) {
        return collisionList[index].right();
    }

    public int getCollisionIndex(String name) {
        return collisionMap.get(name).firstInt();
    }

    public int getCollisionCount() {
        return collisionList.length;
    }

    @Override
    public boolean disable() {
        return disable;
    }
}
