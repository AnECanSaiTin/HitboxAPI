package cn.anecansaitin.hitboxapi.colliders;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class CollisionUtil {
    /**
     * 获取线段上最近待判定点的坐标
     *
     * @param start 线段起点
     * @param end   线段终点
     * @param point 待判定点
     * @return 线段上最接近判定点的坐标
     */
    public static Vector3f getClosestPointOnSegment(Vector3f start, Vector3f end, Vector3f point) {
        Vector3f se = end.sub(start, new Vector3f());
        Vector3f sp = point.sub(start, new Vector3f());
        float f = se.dot(sp) / se.lengthSquared();
        f = Math.min(Math.max(f, 0), 1);
        return se.mul(f).add(start, sp);
    }

    /**
     * 判断线段相交
     *
     * @param start1 线段1起点
     * @param end1   线段1终点
     * @param start2 线段2起点
     * @param end2   线段2终点
     * @return 相交返回true
     */
    private static boolean isSegmentCross(Vector3f start1, Vector3f end1, Vector3f start2, Vector3f end2) {
        if (Math.min(start1.x, end1.x) - Math.max(start2.x, end2.x) > 0.01 ||
                Math.min(start1.y, end1.y) - Math.max(start2.y, end2.y) > 0.01 ||
                Math.min(start1.z, end1.z) - Math.max(start2.z, end2.z) > 0.01 ||
                Math.min(start2.x, end2.x) - Math.max(start1.x, end1.x) > 0.01 ||
                Math.min(start2.y, end2.y) - Math.max(start1.y, end1.y) > 0.01 ||
                Math.min(start2.z, end2.z) - Math.max(start1.z, end1.z) > 0.01) {
            return false;
        }

        Vector3f line1 = end1.sub(start1, new Vector3f());
        Vector3f line2 = end2.sub(start2, new Vector3f());
        Vector3f v1 = new Vector3f(),
                v2 = new Vector3f();

        return !line1.cross(start2.sub(start1, v1), v1).normalize().equals(line1.cross(end2.sub(start1, v2), v2).normalize()) &&
                !line2.cross(start1.sub(start2, v1), v1).normalize().equals(line2.cross(end1.sub(start2, v2), v2).normalize());
    }

    /**
     * 计算两个线段最近距离的平方
     *
     * @param start1 线段1起点
     * @param end1   线段1终点
     * @param start2 线段2起点
     * @param end2   线段2终点
     * @return 两个线段最近距离的平方
     */
    public static float getClosestDistanceBetweenSegmentsSqr(Vector3f start1, Vector3f end1, Vector3f start2, Vector3f end2) {
        Vector3f line1 = end1.sub(start1, new Vector3f());
        Vector3f line2 = end2.sub(start2, new Vector3f());
        Vector3f v1 = new Vector3f(),
                v2 = new Vector3f();

        float dis;
        //判断完全平行
        boolean isParallel = line1.normalize(v1).equals(line2.normalize(v2));

        if (isParallel) {
            //完全平行
            float len1 = line1.lengthSquared();
            float len2 = line2.lengthSquared();

            float disStart;
            float disEnd;

            if (len1 > len2) {
                disStart = (getClosestPointOnSegment(start1, end1, start2).sub(start2, v1)).lengthSquared();
                disEnd = (getClosestPointOnSegment(start1, end1, end2).sub(end2, v1)).lengthSquared();
            } else {
                disStart = (getClosestPointOnSegment(start2, end2, start1).sub(start1, v1)).lengthSquared();
                disEnd = (getClosestPointOnSegment(start2, end2, end1).sub(end1, v1)).lengthSquared();
            }

            dis = Math.min(disStart, disEnd);
        } else {
            Vector3f normal = line1.cross(line2, new Vector3f());
            float len = normal.lengthSquared();
            float dis2Line = (float) (Math.pow(Math.abs(start2.sub(start1, v1).dot(normal)), 2) / len);
            //判断同面
            if (dis2Line == 0) {
                //同面
                // 检测线段相交
                boolean isLineCross = isSegmentCross(start1, end1, start2, end2);

                if (isLineCross) {
                    dis = 0;
                } else {
                    float disStart1 = getClosestPointOnSegment(start1, end1, start2).sub(start2).lengthSquared();
                    float disEnd1 = getClosestPointOnSegment(start1, end1, end2).sub(end2).lengthSquared();
                    float disStart2 = getClosestPointOnSegment(start2, end2, start1).sub(start1).lengthSquared();
                    float disEnd2 = getClosestPointOnSegment(start2, end2, end1).sub(end1).lengthSquared();
                    dis = Math.min(Math.min(disStart1, disEnd1), Math.min(disStart2, disEnd2));
                }
            } else {
                float offset = (float) Math.sqrt(dis2Line);
                //计算line2相对line1的方位
                Vector3f directionStart = start2.sub(start1, v1);
                float direction = directionStart.dot(normal) > 0 ? 1 : -1;
                // 检测线段相交
                boolean isLineCross = isSegmentCross(start1, end1, start2.sub(normal.normalize(v1).mul(offset * direction), v1),
                        end2.sub(normal.normalize(v2).mul(offset * direction)));

                if (isLineCross) {
                    dis = dis2Line;
                } else {
                    float disStart1 = getClosestPointOnSegment(start1, end1, start2).sub(start2).lengthSquared();
                    float disEnd1 = getClosestPointOnSegment(start1, end1, end2).sub(end2).lengthSquared();
                    float disStart2 = getClosestPointOnSegment(start2, end2, start1).sub(start1).lengthSquared();
                    float disEnd2 = getClosestPointOnSegment(start2, end2, end1).sub(end1).lengthSquared();
                    dis = Math.min(Math.min(disStart1, disEnd1), Math.min(disStart2, disEnd2));
                }
            }
        }

        return dis;
    }

    /**
     * 计算OBB上离待判定点最近的点
     *
     * @param point 待判定点
     * @param obb   OBB盒
     * @return 在OBB上离待判定点最近的点
     */
    public static Vector3f getClosestPointOBB(Vector3f point, OBB obb) {
        Vector3f nearP = obb.getCenter();
        //求球心与OBB中心的距离向量 从OBB中心指向球心
        Vector3f center2 = obb.getCenter();
        Vector3f dist = point.sub(center2);

        float[] extents = new float[]{obb.getHalfExtents().x, obb.getHalfExtents().y, obb.getHalfExtents().z};
        Vector3f[] axes = obb.getRotationV();

        for (int i = 0; i < 3; i++) {
            //计算距离向量到OBB坐标轴的投影长度 即距离向量在OBB坐标系中的对应坐标轴的长度
            float distance = dist.dot(axes[i]);
            distance = Math.clamp(distance, -extents[i], extents[i]);
            //还原到世界坐标
            nearP.x += distance * axes[i].x;
            nearP.y += distance * axes[i].y;
            nearP.z += distance * axes[i].z;
        }

        return nearP;
    }

    private static boolean getSeparatingPlane(Vector3f RPos, Vector3f Plane, OBB box1, OBB box2) {
        Vector3f v = new Vector3f();

        return (Math.abs(RPos.dot(Plane)) >
                (Math.abs((box1.getRotationV()[0].mul(box1.getHalfExtents().x, v)).dot(Plane)) +
                        Math.abs((box1.getRotationV()[1].mul(box1.getHalfExtents().y, v)).dot(Plane)) +
                        Math.abs((box1.getRotationV()[2].mul(box1.getHalfExtents().z, v)).dot(Plane)) +
                        Math.abs((box2.getRotationV()[0].mul(box2.getHalfExtents().x, v)).dot(Plane)) +
                        Math.abs((box2.getRotationV()[1].mul(box2.getHalfExtents().y, v)).dot(Plane)) +
                        Math.abs((box2.getRotationV()[2].mul(box2.getHalfExtents().z, v)).dot(Plane))));
    }

    /**
     * 判断两个胶囊体是否碰撞
     *
     * @param capsule 胶囊体
     * @param other   胶囊体
     * @return 碰撞返回true
     */
    public static boolean isCollision(Capsule capsule, Capsule other) {
        //计算头尾点最值
        Vector3f pointA1 = capsule.getDirection().mul(capsule.getHeight(), new Vector3f()).add(capsule.getCenter());
        Vector3f pointA2 = capsule.getDirection().mul(-capsule.getHeight(), new Vector3f()).add(capsule.getCenter());

        Vector3f pointB1 = other.getDirection().mul(other.getHeight(), new Vector3f()).add(other.getCenter());
        Vector3f pointB2 = other.getDirection().mul(-other.getHeight(), new Vector3f()).add(other.getCenter());

        // 求两条线段的最短距离
        float distance = CollisionUtil.getClosestDistanceBetweenSegmentsSqr(pointA1, pointA2, pointB1, pointB2);

        //求两个球半径和
        float totalRadius = (float) Math.pow(capsule.getRadius() + other.getRadius(), 2);
        //距离小于等于半径和则碰撞
        return distance <= totalRadius;
    }

    /**
     * 判断胶囊体与球体是否碰撞
     *
     * @param capsule 胶囊体
     * @param sphere  球体
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Capsule capsule, Sphere sphere) {
        //计算头尾点最值
        Vector3f point1 = capsule.getDirection().mul(capsule.getHeight(), new Vector3f()).add(capsule.getCenter());
        Vector3f point2 = capsule.getDirection().mul(-capsule.getHeight(), new Vector3f()).add(capsule.getCenter());

        Vector3f closest = CollisionUtil.getClosestPointOnSegment(point1, point2, sphere.getCenter());

        //求两个球半径和
        float totalRadius = (float) Math.pow(capsule.getRadius() + sphere.getRadius(), 2);
        //球两个球心之间的距离
        float distance = closest.sub(sphere.getCenter()).lengthSquared();
        //距离小于等于半径和则碰撞
        return distance <= totalRadius;
    }

    /**
     * 判断胶囊体与OBB是否碰撞
     *
     * @param capsule 胶囊体
     * @param obb     OBB盒
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Capsule capsule, OBB obb) {
        //计算头尾点最值
        Vector3f point1 = capsule.getDirection().mul(capsule.getHeight(), new Vector3f()).add(capsule.getCenter());
        Vector3f point2 = capsule.getDirection().mul(-capsule.getHeight(), new Vector3f()).add(capsule.getCenter());

        Vector3f closest1 = getClosestPointOnSegment(point1, point2, obb.getCenter());
        Vector3f closest2 = getClosestPointOBB(closest1, obb);

        //求胶囊体半径平方
        float totalRadius = (float) Math.pow(capsule.getRadius(), 2);
        //求两个点之间的距离
        float distance = (closest1.sub(closest2)).lengthSquared();
        //距离小于等于半径平方则碰撞
        return distance <= totalRadius;
    }

    /**
     * 判断OBB与OBB是否碰撞
     *
     * @param obb   OBB盒
     * @param other OBB盒
     * @return 有碰撞返回true
     */
    public static boolean isCollision(OBB obb, OBB other) {
        Vector3f RPos = other.getCenter().sub(obb.getCenter(), new Vector3f());
        Vector3f v = new Vector3f();

        return !(getSeparatingPlane(RPos, obb.getRotationV()[0], obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[1], obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[2], obb, other) ||
                getSeparatingPlane(RPos, other.getRotationV()[0], obb, other) ||
                getSeparatingPlane(RPos, other.getRotationV()[1], obb, other) ||
                getSeparatingPlane(RPos, other.getRotationV()[2], obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[0].cross(other.getRotationV()[0], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[0].cross(other.getRotationV()[1], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[0].cross(other.getRotationV()[2], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[1].cross(other.getRotationV()[0], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[1].cross(other.getRotationV()[1], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[1].cross(other.getRotationV()[2], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[2].cross(other.getRotationV()[0], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[2].cross(other.getRotationV()[1], v), obb, other) ||
                getSeparatingPlane(RPos, obb.getRotationV()[2].cross(other.getRotationV()[2], v), obb, other));
    }

    /**
     * 判断球体与OBB是否碰撞
     *
     * @param sphere 球体
     * @param obb    OBB盒
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Sphere sphere, OBB obb) {
        //求最近点
        Vector3f nearP = getClosestPointOBB(sphere.getCenter(), obb);
        //与AABB检测原理相同
        float distance = nearP.sub(sphere.getCenter()).lengthSquared();
        float radius = (float) Math.pow(sphere.getRadius(), 2);
        return distance <= radius;
    }

    /**
     * 判断球体与球体是否碰撞
     * @param sphere 球体
     * @param other 球体
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Sphere sphere, Sphere other) {
        return sphere.getCenter().distanceSquared(other.getCenter()) <= Math.pow(sphere.getRadius() + other.getRadius(), 2);
    }
}
