package cn.anecansaitin.hitboxapi;

import org.joml.Vector3f;

public final class CollisionUtil {
    public static Vector3f getClosestPointOnSegment(Vector3f start, Vector3f end, Vector3f point) {
        Vector3f se = end.sub(start, new Vector3f());
        Vector3f sp = point.sub(start, new Vector3f());
        float f = se.dot(sp) / se.lengthSquared();
        f = Math.min(Math.max(f, 0), 1);
        return se.mul(f).add(start, sp);
    }

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
}
