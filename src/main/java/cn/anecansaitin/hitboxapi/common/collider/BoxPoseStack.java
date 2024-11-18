package cn.anecansaitin.hitboxapi.common.collider;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 坐标变换栈<br/>
 * <br/>
 * 用于对碰撞箱进行坐标系的变换。储存局部坐标系所在父坐标系的位置与旋转信息。
 *
 * <pre>{@code
 *      Entity A = ...;
 *      Entity B = ...;
 *
 *      //假设有数据附加能获取碰撞箱
 *      ICollider colliderA = A.getData(...);
 *      ICollider colliderB = B.getData(...);
 *
 *      //碰撞箱为实体局部坐标系，其父坐标系为世界坐标系，因此存入实体的世界坐标。旋转同理。
 *      BoxPoseStack poseStackA = new BoxPoseStack();
 *      poseStackA.setPosition(A.position().toVector3f());
 *      BoxPoseStack poseStackB = new BoxPoseStack();
 *      poseStackB.setPosition(B.position().toVector3f());
 *
 *      //判断碰撞箱是否相交
 *      boolean result = ColliderUtil.isColliding(colliderA, poseStackA, colliderB, poseStackB);
 *  }</pre>
 *
 *  <br/>
 *
 *  为便于复合碰撞箱的嵌套，提供了栈功能，用法类似与原版渲染PoseStack。<br/>
 *  {@link cn.anecansaitin.hitboxapi.common.collider.Composite#prepareColliding(cn.anecansaitin.hitboxapi.common.collider.BoxPoseStack)}
 */
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
