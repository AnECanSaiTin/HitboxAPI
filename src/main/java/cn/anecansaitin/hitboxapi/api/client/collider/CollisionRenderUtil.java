package cn.anecansaitin.hitboxapi.api.client.collider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public final class CollisionRenderUtil {
    private static final float[][][] capsuleVerticesCache = {
            generateHalfSphere(1, 10, 20, true),
            generateHalfSphere(1, 10, 20, false),
            generateCylinder(1, 1, 20)
    };

    private static final float[][] sphereVerticesCache = generateSphereWireframe(1, 21, 21);

    /// 绘制 AABB
    public static void renderAABB(PoseStack poseStack, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        LevelRenderer.renderLineBox(poseStack, buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
    }

    /// 绘制胶囊体
    public static void renderCapsule(PoseStack poseStack, VertexConsumer buffer, float centerX, float centerY, float centerZ, float radius, float height, Quaternionf rotation, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);
        poseStack.mulPose(rotation);
        PoseStack.Pose pose = poseStack.last();
        float yOffset = height / 2;

        //球
        for (int i = 0; i < 2; i++) {
            float[][] vertices = capsuleVerticesCache[i];
            float[] vertex = vertices[0];
            float[] normal = vertices[1];

            for (int j = 0; j < vertex.length; j += 3) {
                float x = vertex[j] * radius;
                float y = vertex[j + 1] * radius + yOffset;
                float z = vertex[j + 2] * radius;

                float nx = normal[j];
                float ny = normal[j + 1];
                float nz = normal[j + 2];

                buffer.addVertex(pose, x, y, z).setColor(red, green, blue, alpha).setNormal(pose, nx, ny, nz);
            }

            yOffset = -yOffset;
        }

        //圆柱
        float[][] vertices = capsuleVerticesCache[2];
        float[] vertex = vertices[0];

        for (int j = 0; j < vertex.length; j += 3) {
            float x = vertex[j] * radius;
            float y = vertex[j + 1] * height;
            float z = vertex[j + 2] * radius;

            buffer.addVertex(pose, x, y, z).setColor(red, green, blue, alpha).setNormal(pose, 0, 1, 0);
        }

        poseStack.popPose();
    }

    /// 绘制 OBB
    public static void renderOBB(PoseStack poseStack, VertexConsumer buffer, float centerX, float centerY, float centerZ, Quaternionf rotation, float halfX, float halfY, float halfZ, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);
        poseStack.mulPose(rotation);
        LevelRenderer.renderLineBox(poseStack, buffer, -halfX, -halfY, -halfZ, halfX, halfY, halfZ, red, green, blue, alpha);
        poseStack.popPose();
    }

    /// 绘制射线
    public static void renderRay(PoseStack poseStack, VertexConsumer buffer, float originX, float originY, float originZ, float endX, float endY, float endZ, float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        float directionX = endX - originX;
        float directionY = endY - originY;
        float directionZ = endZ - originZ;
        float scalar = org.joml.Math.invsqrt(org.joml.Math.fma(directionX, directionX, org.joml.Math.fma(directionY, directionY, directionZ * directionZ)));
        directionX *= scalar;
        directionY *= scalar;
        directionZ *= scalar;
        buffer.addVertex(pose, originX, originY, originZ).setColor(red, green, blue, alpha).setNormal(pose, directionX, directionY, directionZ);
        buffer.addVertex(pose, endX, endY, endZ).setColor(red, green, blue, alpha).setNormal(pose, directionX, directionY, directionZ);
    }

    /// 绘制球体
    public static void renderSphere(PoseStack poseStack, VertexConsumer buffer, float centerX, float centerY, float centerZ, float radius, float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        float[] vertex = sphereVerticesCache[0];
        float[] normal = sphereVerticesCache[1];

        for (int i = 0; i < vertex.length; i += 3) {
            float x = vertex[i] * radius + centerX;
            float y = vertex[i + 1] * radius + centerY;
            float z = vertex[i + 2] * radius + centerZ;

            float nx = normal[i];
            float ny = normal[i + 1];
            float nz = normal[i + 2];

            buffer.addVertex(pose, x, y, z).setColor(red, green, blue, alpha).setNormal(pose, nx, ny, nz);
        }
    }

    public static float[][] generateHalfSphere(float radius, int stacks, int slices, boolean isUpperHalf) {
        int vertexCount = (stacks + 1) * (slices + 1);
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        List<Integer> indices = new ArrayList<>();
        float phiStep = (float) Math.PI / (2 * stacks); // 半个球的 phi 步长
        float thetaStep = (float) (2 * Math.PI) / slices;

        int index = 0;
        for (int i = 0; i <= stacks; i++) {
            float phi;
            if (isUpperHalf) {
                phi = i * phiStep; // 上半球
            } else {
                phi = (float) Math.PI / 2 + i * phiStep; // 下半球
            }
            for (int j = 0; j <= slices; j++) {
                float theta = j * thetaStep;
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);
                float sinTheta = (float) Math.sin(theta);
                float cosTheta = (float) Math.cos(theta);

                float x = radius * sinPhi * cosTheta;
                float y = radius * cosPhi;
                float z = radius * sinPhi * sinTheta;

                vertices[index * 3] = x;
                vertices[index * 3 + 1] = y;
                vertices[index * 3 + 2] = z;

                normals[index * 3] = x;
                normals[index * 3 + 1] = y;
                normals[index * 3 + 2] = z;

                index++;
            }
        }

        // 生成线段索引
        for (int i = 0; i <= stacks; i++) {
            for (int j = 0; j <= slices; j++) {
                int current = i * (slices + 1) + j;
                if (j < slices) {
                    indices.add(current);
                    indices.add(current + 1);
                }
                if (i < stacks) {
                    indices.add(current);
                    indices.add(current + slices + 1);
                }
            }
        }

        // 按线段顺序重新排列顶点和法线
        float[] orderedVertices = new float[indices.size() * 3];
        float[] orderedNormals = new float[indices.size() * 3];
        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            orderedVertices[i * 3] = vertices[idx * 3];
            orderedVertices[i * 3 + 1] = vertices[idx * 3 + 1];
            orderedVertices[i * 3 + 2] = vertices[idx * 3 + 2];

            orderedNormals[i * 3] = normals[idx * 3];
            orderedNormals[i * 3 + 1] = normals[idx * 3 + 1];
            orderedNormals[i * 3 + 2] = normals[idx * 3 + 2];
        }

        return new float[][]{orderedVertices, orderedNormals};
    }

    public static float[][] generateCylinder(float radius, float height, int slices) {
        int vertexCount = (slices + 1) * 2; // 侧面顶点
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        List<Integer> indices = new ArrayList<>();
        float thetaStep = (float) (2 * Math.PI) / slices;

        int index = 0;

        // 生成侧面顶点
        for (int j = 0; j <= slices; j++) {
            float theta = j * thetaStep;
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);

            float x = radius * cosTheta;
            float z = radius * sinTheta;

            // 底面顶点
            vertices[index * 3] = x;
            vertices[index * 3 + 1] = -height / 2;
            vertices[index * 3 + 2] = z;

            normals[index * 3] = cosTheta;
            normals[index * 3 + 1] = 0;
            normals[index * 3 + 2] = sinTheta;

            index++;

            // 顶面顶点
            vertices[index * 3] = x;
            vertices[index * 3 + 1] = height / 2;
            vertices[index * 3 + 2] = z;

            normals[index * 3] = cosTheta;
            normals[index * 3 + 1] = 0;
            normals[index * 3 + 2] = sinTheta;

            index++;
        }

        // 生成侧面竖直线段索引
        for (int j = 0; j <= slices; j++) {
            int bottom = j * 2;
            int top = bottom + 1;
            indices.add(bottom);
            indices.add(top);
        }

        // 按线段顺序重新排列顶点和法线
        float[] orderedVertices = new float[indices.size() * 3];
        float[] orderedNormals = new float[indices.size() * 3];
        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            orderedVertices[i * 3] = vertices[idx * 3];
            orderedVertices[i * 3 + 1] = vertices[idx * 3 + 1];
            orderedVertices[i * 3 + 2] = vertices[idx * 3 + 2];

            orderedNormals[i * 3] = normals[idx * 3];
            orderedNormals[i * 3 + 1] = normals[idx * 3 + 1];
            orderedNormals[i * 3 + 2] = normals[idx * 3 + 2];
        }

        return new float[][]{orderedVertices, orderedNormals};
    }

    public static float[][] generateSphereWireframe(float radius, int stacks, int slices) {
        int vertexCount = (stacks + 1) * (slices + 1);
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        List<Integer> indices = new ArrayList<>();
        float phiStep = (float) Math.PI / stacks;
        float thetaStep = (float) (2 * Math.PI) / slices;

        int index = 0;
        for (int i = 0; i <= stacks; i++) {
            float phi = i * phiStep;
            for (int j = 0; j <= slices; j++) {
                float theta = j * thetaStep;
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);
                float sinTheta = (float) Math.sin(theta);
                float cosTheta = (float) Math.cos(theta);

                float x = radius * sinPhi * cosTheta;
                float y = radius * cosPhi;
                float z = radius * sinPhi * sinTheta;

                vertices[index * 3] = x;
                vertices[index * 3 + 1] = y;
                vertices[index * 3 + 2] = z;

                normals[index * 3] = x;
                normals[index * 3 + 1] = y;
                normals[index * 3 + 2] = z;

                index++;
            }
        }

        // 生成线段索引
        for (int i = 0; i <= stacks; i++) {
            for (int j = 0; j <= slices; j++) {
                int current = i * (slices + 1) + j;
                if (j < slices) {
                    indices.add(current);
                    indices.add(current + 1);
                }
                if (i < stacks) {
                    indices.add(current);
                    indices.add(current + slices + 1);
                }
            }
        }

        // 按线段顺序重新排列顶点和法线
        float[] orderedVertices = new float[indices.size() * 3];
        float[] orderedNormals = new float[indices.size() * 3];
        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            orderedVertices[i * 3] = vertices[idx * 3];
            orderedVertices[i * 3 + 1] = vertices[idx * 3 + 1];
            orderedVertices[i * 3 + 2] = vertices[idx * 3 + 2];

            orderedNormals[i * 3] = normals[idx * 3];
            orderedNormals[i * 3 + 1] = normals[idx * 3 + 1];
            orderedNormals[i * 3 + 2] = normals[idx * 3 + 2];
        }

        return new float[][]{orderedVertices, orderedNormals};
    }
}
