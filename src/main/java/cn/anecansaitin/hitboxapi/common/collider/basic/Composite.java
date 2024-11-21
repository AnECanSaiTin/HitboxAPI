package cn.anecansaitin.hitboxapi.common.collider.basic;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.IComposite;
import cn.anecansaitin.hitboxapi.common.collider.BoxPoseStack;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;

public class Composite<T, D> implements IComposite<T, D> {
    public final Vector3f position;
    public final Quaternionf rotation;
    public final HashMap<String, IntObjectPair<ICollider<T, D>>> collisionMap = new HashMap<>();
    public ICollider<T, D>[] colliders;
    public String[] colliderNames;
    public final Vector3f globalPosition;
    public final Quaternionf globalRotation;

    public boolean disable;
    private boolean isDirty;

    @SuppressWarnings("unchecked")
    public Composite(Vector3f position, Quaternionf rotation, List<Pair<String, ICollider<T, D>>> collisions) {
        this.position = position;
        this.globalPosition = new Vector3f(position);
        this.rotation = rotation;
        this.globalRotation = new Quaternionf(rotation);
        this.colliders = new ICollider[collisions.size()];
        this.colliderNames = new String[collisions.size()];

        for (int i = 0, collisionsSize = collisions.size(); i < collisionsSize; i++) {
            Pair<String, ICollider<T, D>> pair = collisions.get(i);
            this.colliderNames[i] = pair.left();
            this.colliders[i] = pair.right();
            collisionMap.put(pair.left(), new IntObjectImmutablePair<>(i, pair.right()));
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

        for (ICollider<T, D> collider : colliders) {
            collider.prepareColliding(poseStack);
        }

        poseStack.pop();
    }

    public void markDirty() {
        isDirty = true;
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public ICollider<T, D> getCollider(String name) {
        return collisionMap.get(name).right();
    }

    @Override
    public ICollider<T, D> getCollider(int index) {
        return colliders[index];
    }

    public int getCollisionIndex(String name) {
        return collisionMap.get(name).firstInt();
    }

    @Override
    public int getCollidersCount() {
        return colliders.length;
    }

    @Override
    public boolean disable() {
        return disable;
    }

    @Override
    public Vector3f getLocalPosition() {
        return position;
    }

    @Override
    public Quaternionf getLocalRotation() {
        return rotation;
    }

    @Override
    public Vector3f getGlobalPosition() {
        return globalPosition;
    }

    @Override
    public Quaternionf getGlobalRotation() {
        return globalRotation;
    }
}
