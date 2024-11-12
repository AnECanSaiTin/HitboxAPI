package cn.anecansaitin.hitboxapi.common.colliders;

import net.minecraft.world.phys.AABB;
import org.joml.Intersectionf;
import org.joml.Vector3f;

public final class CollisionUtil {
    private static final BoxPoseStack EMPTY_POSE_STACK = new BoxPoseStack();

    public static boolean isColliding(ICollision collision, BoxPoseStack poseStack, ICollision other, BoxPoseStack otherPoseStack) {
        if (other == null) return false;
        if (other == collision) return true;
        if (collision.disable() || other.disable()) return false;

        collision.preIsColliding(poseStack);
        other.preIsColliding(otherPoseStack);

        switch (collision.getType()) {
            case OBB -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isColliding((OBB) collision, (OBB) other);
                    case SPHERE -> CollisionUtil.isColliding((Sphere) other, (OBB) collision);
                    case CAPSULE -> CollisionUtil.isColliding((Capsule) other, (OBB) collision);
                    case AABB -> CollisionUtil.isColliding((OBB) collision, new OBB((AABB) other));
                    case RAY -> CollisionUtil.isColliding((Ray) other, (OBB) collision);
                    case COMPOSITE -> CollisionUtil.isColliding((Composite) other, collision);
                };
            }
            case SPHERE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isColliding((Sphere) collision, (OBB) other);
                    case SPHERE -> CollisionUtil.isColliding((Sphere) collision, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isColliding((Capsule) other, (Sphere) collision);
                    case AABB -> CollisionUtil.isColliding((Sphere) collision, (AABB) other);
                    case RAY -> CollisionUtil.isColliding((Ray) other, (Sphere) collision);
                    case COMPOSITE -> CollisionUtil.isColliding((Composite) other, collision);
                };
            }
            case CAPSULE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isColliding((Capsule) collision, (OBB) other);
                    case SPHERE -> CollisionUtil.isColliding((Capsule) collision, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isColliding((Capsule) collision, (Capsule) other);
                    case AABB -> CollisionUtil.isColliding((Capsule) collision, (AABB) other);
                    case RAY -> CollisionUtil.isColliding((Ray) other, (Capsule) collision);
                    case COMPOSITE -> CollisionUtil.isColliding((Composite) other, collision);
                };
            }
            case AABB -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isColliding((OBB) other, new OBB((AABB) collision));
                    case SPHERE -> CollisionUtil.isColliding((Sphere) other, (AABB) collision);
                    case CAPSULE -> CollisionUtil.isColliding((Capsule) other, (AABB) collision);
                    case AABB -> ((AABB) collision).intersects((AABB) other);
                    case RAY -> CollisionUtil.isColliding((Ray) other, (AABB) collision);
                    case COMPOSITE -> CollisionUtil.isColliding((Composite) other, collision);
                };
            }
            case RAY -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isColliding((Ray) collision, (OBB) other);
                    case SPHERE -> CollisionUtil.isColliding((Ray) collision, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isColliding((Ray) collision, (Capsule) other);
                    case AABB -> CollisionUtil.isColliding((Ray) collision, (AABB) other);
                    case RAY -> CollisionUtil.isColliding((Ray) collision, (Ray) other);
                    case COMPOSITE -> CollisionUtil.isColliding((Composite) other, collision);
                };
            }
            case COMPOSITE -> {
                return CollisionUtil.isColliding((Composite) collision, other);
            }
            default -> {
                return false;
            }
        }
    }

    public static boolean isColliding(ICollision collision, ICollision other) {
        return isColliding(collision, EMPTY_POSE_STACK, other, EMPTY_POSE_STACK);
    }

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
        Vector3f u = new Vector3f(end1).sub(start1);
        Vector3f v = new Vector3f(end2).sub(start2);
        Vector3f w = new Vector3f(start1).sub(start2);

        float a = u.dot(u); // u*u
        float b = u.dot(v); // u*v
        float c = v.dot(v); // v*v
        float d = u.dot(w); // u*w
        float e = v.dot(w); // v*w
        float dt = a * c - b * b;

        float sd = dt;
        float td = dt;

        float sn; // sn = be-cd
        float tn; // tn = ae-bd

        if (Math.abs(dt - 0) < 1e-6) {
            // 两直线平行
            sn = 0;    // 在s上指定取s0
            sd = 1;   // 防止计算时除0错误

            tn = e;      // 按(公式3)求tc
            td = c;
        } else {
            sn = (b * e - c * d);
            tn = (a * e - b * d);
            if (sn < 0) {
                // 最近点在s起点以外，同平行条件
                sn = 0;
                tn = e;
                td = c;
            } else if (sn > sd) {
                // 最近点在s终点以外(即sc>1,则取sc=1)
                sn = sd;
                tn = e + b; // 按(公式3)计算
                td = c;
            }
        }
        if (tn < 0.0) {
            // 最近点在t起点以外
            tn = 0;
            if (-d < 0) // 按(公式2)计算，如果等号右边小于0，则sc也小于零，取sc=0
                sn = 0;
            else if (-d > a) // 按(公式2)计算，如果sc大于1，取sc=1
                sn = sd;
            else {
                sn = -d;
                sd = a;
            }
        } else if (tn > td) {
            tn = td;
            if ((-d + b) < 0.0)
                sn = 0;
            else if ((-d + b) > a)
                sn = sd;
            else {
                sn = (-d + b);
                sd = a;
            }
        }

        float sc;
        float tc;

        if (Math.abs(sn - 0) < 1e-6) sc = 0.0F;
        else sc = sn / sd;

        if (Math.abs(tn - 0) < 1e-6) tc = 0.0F;
        else tc = tn / td;

        Vector3f dP = new Vector3f(w).add(u.mul(sc)).sub(v.mul(tc));
        return dP.dot(dP);
    }

    /**
     * 计算OBB上离待判定点最近的点
     *
     * @param point 待判定点
     * @param obb   OBB盒
     * @return 在OBB上离待判定点最近的点
     */
    public static Vector3f getClosestPointOBB(Vector3f point, OBB obb) {
        Vector3f nearP = new Vector3f(obb.globalCenter);
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
    public static boolean isColliding(Capsule capsule, Capsule other) {
        //计算头尾点最值
        float h = capsule.height / 2;
        Vector3f pointA1 = capsule.direction.mul(h, new Vector3f()).add(capsule.globalCenter);
        Vector3f pointA2 = capsule.direction.mul(-h, new Vector3f()).add(capsule.globalCenter);

        h = other.height / 2;
        Vector3f pointB1 = other.direction.mul(h, new Vector3f()).add(other.globalCenter);
        Vector3f pointB2 = other.direction.mul(-h, new Vector3f()).add(other.globalCenter);

        // 求两条线段的最短距离
        float distance = getClosestDistanceBetweenSegmentsSqr(pointA1, pointA2, pointB1, pointB2);

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
    public static boolean isColliding(Capsule capsule, Sphere sphere) {
        //计算头尾点最值
        float height = capsule.height / 2;
        Vector3f point1 = capsule.direction.mul(height, new Vector3f()).add(capsule.globalCenter);
        Vector3f point2 = capsule.direction.mul(-height, new Vector3f()).add(capsule.globalCenter);

        Vector3f closest = CollisionUtil.getClosestPointOnSegment(point1, point2, sphere.globalCenter);

        //求两个球半径和
        float totalRadius = (float) Math.pow(capsule.radius + sphere.radius, 2);
        //球两个球心之间的距离
        float distance = closest.sub(sphere.globalCenter).lengthSquared();
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
    public static boolean isColliding(Capsule capsule, OBB obb) {
        //计算头尾点最值
        float height = capsule.height / 2;
        Vector3f point1 = capsule.direction.mul(height, new Vector3f()).add(capsule.globalCenter);
        Vector3f point2 = capsule.direction.mul(-height, new Vector3f()).add(capsule.globalCenter);

        Vector3f closest1 = getClosestPointOnSegment(point1, point2, obb.globalCenter);
        Vector3f closest2 = getClosestPointOBB(closest1, obb);

        //求胶囊体半径平方
        float totalRadius = (float) Math.pow(capsule.radius, 2);
        //求两个点之间的距离
        float distance = (closest1.sub(closest2)).lengthSquared();
        //距离小于等于半径平方则碰撞
        return distance <= totalRadius;
    }

    /**
     * 判断OBB盒与OBB盒是否碰撞
     *
     * @param obb   OBB盒
     * @param other OBB盒
     * @return 有碰撞返回true
     */
    public static boolean isColliding(OBB obb, OBB other) {
        //joml居然实现了obb碰撞
        return Intersectionf.testObOb(obb.globalCenter, obb.axes[0], obb.axes[1], obb.axes[2], obb.halfExtents, other.globalCenter, other.axes[0], other.axes[1], other.axes[2], other.halfExtents);
    }

    /**
     * 判断球体与OBB是否碰撞
     *
     * @param sphere 球体
     * @param obb    OBB盒
     * @return 有碰撞返回true
     */
    public static boolean isColliding(Sphere sphere, OBB obb) {
        //求最近点
        Vector3f nearP = getClosestPointOBB(sphere.globalCenter, obb);
        //与AABB检测原理相同
        float distance = nearP.sub(sphere.globalCenter).lengthSquared();
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
    public static boolean isColliding(Sphere sphere, Sphere other) {
        return sphere.globalCenter.distanceSquared(other.globalCenter) <= Math.pow(sphere.radius + other.radius, 2);
    }

    /**
     * 判断球体与AABB盒是否碰撞
     *
     * @param sphere 球体
     * @param aabb   AABB盒
     * @return 有碰撞返回true
     */
    public static boolean isColliding(Sphere sphere, AABB aabb) {
        //求出最近点
        Vector3f center = sphere.globalCenter;
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
    public static boolean isColliding(Capsule capsule, AABB aabb) {
        //计算头尾点最值
        float height = capsule.height / 2;
        Vector3f pointA1 = capsule.direction.mul(height, new Vector3f()).add(capsule.globalCenter);
        Vector3f pointA2 = capsule.direction.mul(-height, new Vector3f()).add(capsule.globalCenter);

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
    public static boolean isColliding(Ray ray, AABB aabb) {
        return Intersectionf.testRayAab(ray.globalOrigin, ray.globalDirection, new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ), new Vector3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ));
    }

    /**
     * 判断射线与球体是否碰撞
     *
     * @param ray    射线
     * @param sphere 球体
     * @return 有碰撞返回true
     */
    public static boolean isColliding(Ray ray, Sphere sphere) {
        return Intersectionf.testRaySphere(ray.globalOrigin, ray.globalDirection, sphere.globalCenter, sphere.radius);
    }

    /**
     * 判断射线与OBB盒是否碰撞
     *
     * @param ray 射线
     * @param obb OBB盒
     * @return 有碰撞返回true
     */
    public static boolean isColliding(Ray ray, OBB obb) {

        Vector3f v = new Vector3f();

        //判断不在OBB内
        Vector3f centerDis = ray.globalOrigin.sub(obb.globalCenter, v);
        float ray2ObbX = centerDis.dot(obb.axes[0]);
        float ray2ObbY = centerDis.dot(obb.axes[1]);
        float ray2ObbZ = centerDis.dot(obb.axes[2]);
        boolean checkNotInside = ray2ObbX < -obb.halfExtents.x || ray2ObbX > obb.halfExtents.x ||
                ray2ObbY < -obb.halfExtents.y || ray2ObbY > obb.halfExtents.y ||
                ray2ObbZ < -obb.halfExtents.z || ray2ObbZ > obb.halfExtents.z;
        //判断反向情况
        boolean checkFoward = obb.globalCenter.sub(obb.globalCenter, v).dot(ray.globalDirection) < 0;

        if (checkNotInside && checkFoward) {
            return false;
        }

        //判断是否相交
        Vector3f min = new Vector3f();
        Vector3f minP = obb.vertices[4].sub(ray.globalOrigin, v);
        min.x = minP.dot(obb.axes[0]);
        min.y = minP.dot(obb.axes[1]);
        min.z = minP.dot(obb.axes[2]);

        Vector3f max = new Vector3f();
        Vector3f maxP = obb.vertices[2].sub(ray.globalOrigin, v);
        max.x = maxP.dot(obb.axes[0]);
        max.y = maxP.dot(obb.axes[1]);
        max.z = maxP.dot(obb.axes[2]);


        Vector3f projection = new Vector3f();
        projection.x = 1 / ray.globalDirection.dot(obb.axes[2]);

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

        if (checkNotInside) {
            return n < f && ray.length >= n;
        }

        return true;
    }

    /**
     * 判断射线与胶囊体是否碰撞
     *
     * @param ray     射线
     * @param capsule 胶囊体
     * @return 有碰撞返回true
     */
    public static boolean isColliding(Ray ray, Capsule capsule) {
        float halfHeight = capsule.height / 2.0f;
        Vector3f startPoint = capsule.direction.mul(-halfHeight, new Vector3f()).add(capsule.globalCenter);
        Vector3f endPoint = capsule.direction.mul(halfHeight, new Vector3f()).add(capsule.globalCenter);
        float sqr = getClosestDistanceBetweenSegmentsSqr(ray.globalOrigin, ray.getEnd(), startPoint, endPoint);
        return sqr <= Math.pow(capsule.radius, 2);
    }

    /**
     * 判断射线与射线是否碰撞<br/>
     * 这有必要吗🤣
     *
     * @param ray   射线
     * @param other 射线
     * @return 有碰撞返回true
     */
    public static boolean isColliding(Ray ray, Ray other) {
        return isSegmentCross(ray.globalOrigin, ray.getEnd(), other.globalOrigin, other.getEnd());
    }

    /**
     * 判断复合碰撞箱与其他碰撞体是否碰撞
     *
     * @param composite 复合碰撞箱
     * @param other     其他碰撞体
     * @return 有碰撞返回true
     */
    public static boolean isColliding(Composite composite, ICollision other) {
        int count = composite.getCollisionCount();

        for (int i = 0; i < count; i++) {
            boolean colliding = other.isColliding(composite.getCollision(i));

            if (colliding) {
                return true;
            }
        }

        return false;
    }
}
