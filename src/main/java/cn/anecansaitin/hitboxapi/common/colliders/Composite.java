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

public final class Composite implements ICollision {
    public final Vector3f position;
    public final Quaternionf rotation;
    private final HashMap<String, IntObjectPair<ICollision>> collisionMap = new HashMap<>();
    private final Pair<String, ICollision>[] collisionList;
    public final Vector3f globalPosition;
    public final Quaternionf globalRotation;

    private boolean isDirty;

    @SuppressWarnings("unchecked")
    public Composite(Vector3f position, Quaternionf rotation, List<Pair<String, ICollision>> collisions) {
        this.position = position;
        this.globalPosition = new Vector3f(position);
        this.rotation = rotation;
        this.globalRotation = new Quaternionf(rotation);
        collisionList = collisions.toArray(new Pair[0]);

        for (Pair<String, ICollision> collision : collisionList) {
            collisionMap.put(collision.left(), new IntObjectImmutablePair<>(collisionList.length, collision.right()));
        }
    }

    @Override
    public void preIsColliding(BoxPoseStack poseStack) {
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

        for (Pair<String, ICollision> collision : collisionList) {
            collision.right().preIsColliding(poseStack);
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

    public ICollision getCollision(String name) {
        return collisionMap.get(name).right();
    }

    public ICollision getCollision(int index) {
        return collisionList[index].right();
    }

    public int getCollisionIndex(String name) {
        return collisionMap.get(name).firstInt();
    }

    public int getCollisionCount() {
        return collisionList.length;
    }
}
