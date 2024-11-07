package cn.anecansaitin.hitboxapi.common.colliders;

import net.minecraft.world.phys.AABB;
import org.joml.Intersectionf;
import org.joml.Vector2f;
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
        Vector3f nearP = new Vector3f(obb.center);
        //求球心与OBB中心的距离向量 从OBB中心指向球心
        Vector3f dist = point.sub(nearP, new Vector3f());

        float[] extents = new float[]{obb.halfExtents.x, obb.halfExtents.y, obb.halfExtents.z};
        Vector3f[] axes = obb.axes;

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

    private static Vector3f getClosestPointAABB(Vector3f point, AABB aabb) {
        Vector3f nearP = new Vector3f();
        nearP.x = (float) Math.clamp(point.x, aabb.minX, aabb.maxX);
        nearP.y = (float) Math.clamp(point.y, aabb.minY, aabb.maxY);
        nearP.z = (float) Math.clamp(point.z, aabb.minZ, aabb.maxZ);
        return nearP;
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
        Vector3f pointA1 = capsule.direction.mul(capsule.height, new Vector3f()).add(capsule.center);
        Vector3f pointA2 = capsule.direction.mul(-capsule.height, new Vector3f()).add(capsule.center);

        Vector3f pointB1 = other.direction.mul(other.height, new Vector3f()).add(other.center);
        Vector3f pointB2 = other.direction.mul(-other.height, new Vector3f()).add(other.center);

        // 求两条线段的最短距离
        float distance = CollisionUtil.getClosestDistanceBetweenSegmentsSqr(pointA1, pointA2, pointB1, pointB2);

        //求两个球半径和
        float totalRadius = (float) Math.pow(capsule.radius + other.radius, 2);
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
        Vector3f point1 = capsule.direction.mul(capsule.height, new Vector3f()).add(capsule.center);
        Vector3f point2 = capsule.direction.mul(-capsule.height, new Vector3f()).add(capsule.center);

        Vector3f closest = CollisionUtil.getClosestPointOnSegment(point1, point2, sphere.center);

        //求两个球半径和
        float totalRadius = (float) Math.pow(capsule.radius + sphere.radius, 2);
        //球两个球心之间的距离
        float distance = closest.sub(sphere.center).lengthSquared();
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
        Vector3f point1 = capsule.direction.mul(capsule.height, new Vector3f()).add(capsule.center);
        Vector3f point2 = capsule.direction.mul(-capsule.height, new Vector3f()).add(capsule.center);

        Vector3f closest1 = getClosestPointOnSegment(point1, point2, obb.center);
        Vector3f closest2 = getClosestPointOBB(closest1, obb);

        //求胶囊体半径平方
        float totalRadius = (float) Math.pow(capsule.radius, 2);
        //求两个点之间的距离
        float distance = (closest1.sub(closest2)).lengthSquared();
        //距离小于等于半径平方则碰撞
        return distance <= totalRadius;
    }

    public static boolean isCollision(OBB obb, OBB other) {
        //joml居然实现了obb碰撞
        return Intersectionf.testObOb(obb.center, obb.axes[0], obb.axes[1], obb.axes[2], obb.halfExtents, other.center, other.axes[0], other.axes[1], other.axes[2], other.halfExtents);
    }

    private static boolean notInteractiveOBB(Vector3f[] vertices1, Vector3f[] vertices2, Vector3f axis) {
        //计算OBB包围盒在分离轴上的投影极限值
        float[] limit1 = getProjectionLimit(vertices1, axis);
        float[] limit2 = getProjectionLimit(vertices2, axis);
        //两个包围盒极限值不相交，则不碰撞
        return limit1[0] > limit2[1] || limit2[0] > limit1[1];
    }

    private static float[] getProjectionLimit(Vector3f[] vertices, Vector3f axis) {
        float[] result = new float[]{Float.MAX_VALUE, Float.MIN_VALUE};

        for (Vector3f vertex : vertices) {
            float dot = vertex.dot(axis);
            result[0] = Math.min(dot, result[0]);
            result[1] = Math.max(dot, result[1]);
        }

        return result;
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
        Vector3f nearP = getClosestPointOBB(sphere.center, obb);
        //与AABB检测原理相同
        float distance = nearP.sub(sphere.center).lengthSquared();
        float radius = (float) Math.pow(sphere.radius, 2);
        return distance <= radius;
    }

    /**
     * 判断球体与球体是否碰撞
     *
     * @param sphere 球体
     * @param other  球体
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Sphere sphere, Sphere other) {
        return sphere.center.distanceSquared(other.center) <= Math.pow(sphere.radius + other.radius, 2);
    }

    /**
     * 判断球体与AABB盒是否碰撞
     *
     * @param sphere 球体
     * @param aabb   AABB盒
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Sphere sphere, AABB aabb) {
        //求出最近点
        Vector3f center = sphere.center;
        Vector3f nearP = getClosestPointAABB(center, aabb);
        //求出最近点与球心的距离
        float distance = nearP.sub(center).lengthSquared();
        float radius = (float) Math.pow(sphere.radius, 2);
        //距离小于半径则碰撞
        return distance <= radius;
    }

    /**
     * 判断胶囊体与AABB盒是否碰撞
     *
     * @param capsule 胶囊体
     * @param aabb    AABB盒
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Capsule capsule, AABB aabb) {
        //计算头尾点最值
        Vector3f pointA1 = capsule.direction.mul(capsule.direction.y, new Vector3f()).add(capsule.center);
        Vector3f pointA2 = capsule.direction.mul(-capsule.direction.y, new Vector3f()).add(capsule.center);

        Vector3f closest1 = getClosestPointOnSegment(pointA1, pointA2, aabb.getCenter().toVector3f());
        Vector3f closest2 = getClosestPointAABB(closest1, aabb);

        //求胶囊体半径平方
        float totalRadius = (float) Math.pow(capsule.radius, 2);
        //求两个点之间的距离
        float distance = closest1.sub(closest2).lengthSquared();

        //距离小于等于半径平方则碰撞
        return distance <= totalRadius;
    }

    /**
     * 判断射线与AABB盒是否碰撞
     *
     * @param ray  射线
     * @param aabb AABB盒
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Ray ray, AABB aabb) {
        return Intersectionf.testRayAab(ray.origin, ray.direction, new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ), new Vector3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ));
    }

    /**
     * 判断射线与球体是否碰撞
     *
     * @param ray    射线
     * @param sphere 球体
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Ray ray, Sphere sphere) {
        return Intersectionf.testRaySphere(ray.origin, ray.direction, sphere.center, sphere.radius);
    }

    /**
     * 判断射线与OBB盒是否碰撞
     *
     * @param ray 射线
     * @param obb OBB盒
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Ray ray, OBB obb) {

        Vector3f v = new Vector3f();

        //判断不在OBB内
        Vector3f centerDis = ray.origin.sub(obb.center, v);
        float ray2ObbX = centerDis.dot(obb.axes[0]);
        float ray2ObbY = centerDis.dot(obb.axes[1]);
        float ray2ObbZ = centerDis.dot(obb.axes[2]);
        boolean checkNotInside = ray2ObbX < -obb.halfExtents.x || ray2ObbX > obb.halfExtents.x ||
                ray2ObbY < -obb.halfExtents.y || ray2ObbY > obb.halfExtents.y ||
                ray2ObbZ < -obb.halfExtents.z || ray2ObbZ > obb.halfExtents.z;
        //判断反向情况
        boolean checkFoward = obb.center.sub(obb.center, v).dot(ray.direction) < 0;

        if (checkNotInside && checkFoward) {
            return false;
        }

        //判断是否相交
        Vector3f min = new Vector3f();
        Vector3f minP = obb.vertices[4].sub(ray.origin, v);
        min.x = minP.dot(obb.axes[0]);
        min.y = minP.dot(obb.axes[1]);
        min.z = minP.dot(obb.axes[2]);

        Vector3f max = new Vector3f();
        Vector3f maxP = obb.vertices[2].sub(ray.origin, v);
        max.x = maxP.dot(obb.axes[0]);
        max.y = maxP.dot(obb.axes[1]);
        max.z = maxP.dot(obb.axes[2]);


        Vector3f projection = new Vector3f();
        projection.x = 1 / ray.direction.dot(obb.axes[2]);

        Vector3f pMin = min.mul(projection);
        Vector3f pMax = max.mul(projection);

        if (projection.x < 0) {
            float t = pMin.x;
            pMin.x = pMax.x;
            pMax.x = t;
        }

        if (projection.y < 0) {
            float t = pMin.y;
            pMin.y = pMax.y;
            pMax.y = t;
        }

        if (projection.z < 0) {
            float t = pMin.z;
            pMin.z = pMax.z;
            pMax.z = t;
        }


        float n = Math.max(Math.max(pMin.x, pMin.y), pMin.z);
        float f = Math.min(Math.min(pMax.x, pMax.y), pMax.z);

        if (!checkNotInside) {
/*
            获得碰撞点
            Vector3f point = ray.direction.mul(f).add(ray.origin);

*/
        } else {
            return n < f && ray.length >= n;

            //获得碰撞点
//            Vector3f point = ray.direction.mul(n, v).add(ray.origin);

        }

        return true;
    }

    public static boolean isCollision(Ray ray, Capsule capsule) {
        float halfHeight = capsule.height / 2.0f;
        Vector3f startPoint = capsule.direction.mul(-halfHeight, new Vector3f()).add(capsule.center);
        Vector3f endPoint = capsule.direction.mul(halfHeight, new Vector3f()).add(capsule.center);
        float sqr = getClosestDistanceBetweenSegmentsSqr(ray.origin, ray.getEnd(), startPoint, endPoint);
        return sqr <= Math.pow(capsule.radius, 2);
    }

    /**
     * 判断射线与射线是否碰撞<br/>
     * 这有必要吗🤣
     * @param ray  射线
     * @param other 射线
     * @return 有碰撞返回true
     */
    public static boolean isCollision(Ray ray, Ray other) {
        return isSegmentCross(ray.origin, ray.getEnd(), other.origin, other.getEnd());
    }
}
