package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.colliders.Capsule;
import cn.anecansaitin.hitboxapi.common.colliders.ICollider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CapsuleRender implements ICollisionRender {
    public static final CapsuleRender INSTANCE = new CapsuleRender();
    private final float[][][] verticesCache = {
            generateHalfSphere(1, 10, 20, true),
            generateHalfSphere(1, 10, 20, false),
            generateCylinder(1, 1, 20)
    };

    private CapsuleRender() {
    }

    @Override
    public void render(ICollider collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        Capsule capsule = (Capsule) collision;
        poseStack.pushPose();
        Vector3f center = capsule.center;
        poseStack.translate(center.x, center.y, center.z);
        poseStack.mulPose(capsule.rotation);
        PoseStack.Pose pose = poseStack.last();
        float radius = capsule.radius;
        float height = capsule.height;
        float yOffset = capsule.height / 2;

        //球
        for (int i = 0; i < 2; i++) {
            float[][] vertices = verticesCache[i];
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
        float[][] vertices = verticesCache[2];
        float[] vertex = vertices[0];

        for (int j = 0; j < vertex.length; j += 3) {
            float x = vertex[j] * radius;
            float y = vertex[j + 1] * height;
            float z = vertex[j + 2] * radius;

            buffer.addVertex(pose, x, y, z).setColor(red, green, blue, alpha).setNormal(pose, 0, 1, 0);
        }

        poseStack.popPose();
    }

    public static float[][] generateHalfSphere(float radius, int stacks, int slices, boolean isUpperHalf) {
        int vertexCount = (stacks + 1) * (slices + 1);
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        List<Integer> indices = new ArrayList<>();
        float phiStep = (float) Math.PI / (2 * stacks); // 半球的 phi 步长
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
}
