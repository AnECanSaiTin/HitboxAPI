package cn.anecansaitin.hitboxapi.common.colliders;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

public class BoxPoseStack {
    private final Deque<BoxPoseStack.Pose> poseStack;

    public BoxPoseStack() {
        Pose pose = new Pose();
        poseStack = new ArrayDeque<>();
        poseStack.add(pose);
    }

    public void setPosition(Vector3f position) {
        last().position.set(position);
    }

    public void setRotation(Quaternionf rotation) {
        last().rotation.set(rotation);
    }

    public void push() {
        poseStack.addLast(last().copy());
    }

    public void pop() {
        poseStack.removeLast();
    }

    public Pose last() {
        return poseStack.getLast();
    }

    public boolean isDirty() {
        return last().isDirty;
    }

    public void setDirty(boolean dirty) {
        last().isDirty = dirty;
    }

    public static final class Pose {
        public final Vector3f position = new Vector3f();
        public final Quaternionf rotation = new Quaternionf();
        public boolean isDirty;

        private Pose copy() {
            Pose pose = new Pose();
            pose.position.set(this.position);
            pose.rotation.set(this.rotation);
            pose.isDirty = this.isDirty;
            return pose;
        }
    }
}
