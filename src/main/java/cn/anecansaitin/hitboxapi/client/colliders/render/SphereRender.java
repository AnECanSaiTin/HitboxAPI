package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import cn.anecansaitin.hitboxapi.common.colliders.Sphere;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class SphereRender implements IColliderRender {
    public static final SphereRender INSTANCE = new SphereRender();
    private final float[][] cachedVertices = generateSphereWireframe(1, 21, 21);

    private SphereRender() {}

    @Override
    public void render(ICollision collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        Sphere sphere = (Sphere) collision;
        PoseStack.Pose pose = poseStack.last();
        float[] vertex = cachedVertices[0];
        float[] normal = cachedVertices[1];

        for (int i = 0; i < vertex.length; i += 3) {
            float x = vertex[i] * sphere.radius + sphere.center.x;
            float y = vertex[i + 1] * sphere.radius + sphere.center.y;
            float z = vertex[i + 2] * sphere.radius + sphere.center.z;

            float nx = normal[i];
            float ny = normal[i + 1];
            float nz = normal[i + 2];

            buffer.addVertex(pose, x, y, z).setColor(red, green, blue, alpha). setNormal(pose, nx, ny, nz);
        }
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
