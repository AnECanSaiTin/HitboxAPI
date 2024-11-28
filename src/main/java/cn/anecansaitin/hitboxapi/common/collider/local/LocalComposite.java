package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalComposite;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalComposite<C extends ILocalCollider<T, D>, T, D> implements ILocalComposite<C, T, D> {
    private final Vector3f localPosition;
    private final Quaternionf localRotation;
    private final HashMap<String, IntObjectPair<C>> colliderMap = new HashMap<>();
    private final ArrayList<C> colliders = new ArrayList<>();
    private final ArrayList<String> colliderNames = new ArrayList<>();
    private final Vector3f globalPosition = new Vector3f();
    private final Quaternionf globalRotation = new Quaternionf();
    private final ICoordinateConverter parent;
    private final short[] version = new short[2];
    private final LocalCompositeCoordinateConverter child = new LocalCompositeCoordinateConverter(this);

    public boolean disable;

    public LocalComposite(Vector3f localPosition, Quaternionf localRotation, ICoordinateConverter parent) {
        this.localPosition = localPosition;
        this.localRotation = localRotation;
        this.parent = parent;
        version[0] = (short) (parent.positionVersion() - 1);
        version[1] = (short) (parent.rotationVersion() - 1);
    }

    @Override
    public Vector3f getLocalPosition() {
        return localPosition;
    }

    @Override
    public void setLocalPosition(Vector3f position) {
        localPosition.set(position);
    }

    @Override
    public Quaternionf getLocalRotation() {
        return localRotation;
    }

    @Override
    public void setLocalRotation(Quaternionf rotation) {
        localRotation.set(rotation);
    }

    @Override
    public Vector3f getPosition() {
        update();
        return globalPosition;
    }

    @Override
    public Quaternionf getRotation() {
        update();
        return globalRotation;
    }

    @Override
    public ICoordinateConverter getConverter() {
        return child;
    }

    @Override
    public int getCollidersCount() {
        return colliders.size();
    }

    @Override
    public C getCollider(int index) {
        return colliders.get(index);
    }

    /// 设置指定索引碰撞箱
    ///
    /// 仅修改碰撞箱，不会影响碰撞箱名称
    ///
    /// @see LocalComposite#setCollider(int index, String name, C collider)
    @Override
    public void setCollider(int index, C collider) {
        colliders.set(index, collider);
        colliderMap.replace(colliderNames.get(index), IntObjectPair.of(index, collider));
    }

    /// 设置指定索引碰撞箱
    ///
    /// 同时修改碰撞箱与名称
    public void setCollider(int index, String name, C collider) {
        colliders.set(index, collider);
        colliderMap.remove(colliderNames.get(index));
        colliderMap.put(name, IntObjectPair.of(index, collider));
        colliderNames.set(index, name);
    }

    /// 添加碰撞箱
    ///
    /// 名称为碰撞箱的索引
    ///
    /// @see LocalComposite#addCollider(String name, C collider)
    @Override
    public void addCollider(C collider) {
        colliders.add(collider);
        String name = String.valueOf(colliders.size());
        colliderNames.add(name);
        colliderMap.put(name, IntObjectPair.of(colliders.size() - 1, collider));
    }

    /// 添加碰撞箱
    public void addCollider(String name, C collider) {
        colliders.add(collider);
        colliderNames.add(name);
        colliderMap.put(name, IntObjectPair.of(colliders.size() - 1, collider));
    }

    /// 添加碰撞箱
    ///
    /// 将碰撞箱添加到指定索引
    ///
    /// 尽量避免使用这个方法，因为需要对所有指定索引之后的缓存进行修改
    public void addCollider(int index, String name, C collider) {
        colliders.add(index, collider);
        colliderNames.add(index, name);

        for (int i = index; i < colliderNames.size(); i++) {
            String key = colliderNames.get(i);
            IntObjectPair<C> pair = colliderMap.get(key);
            colliderMap.replace(key, IntObjectPair.of(pair.leftInt() + 1, pair.right()));
        }
    }

    /// 移除指定索引碰撞箱
    ///
    /// 尽量避免使用这个方法，因为需要对所有指定索引之后的缓存进行修改
    @Override
    public void removeCollider(int index) {
        String name = colliderNames.get(index);
        colliders.remove(index);
        colliderNames.remove(index);
        colliderMap.remove(name);

        for (int i = index; i < colliderNames.size(); i++) {
            String key = colliderNames.get(i);
            IntObjectPair<C> pair = colliderMap.get(key);
            colliderMap.replace(key, IntObjectPair.of(pair.leftInt() - 1, pair.right()));
        }
    }

    /// 移除名称碰撞箱
    ///
    /// 尽量避免使用这个方法，因为需要对所有名称索引之后的缓存进行修改
    public void removeCollider(String name) {
        IntObjectPair<C> pair = colliderMap.remove(name);
        colliders.remove(pair.leftInt());
        colliderNames.remove(name);

        for (int i = pair.leftInt(); i < colliderNames.size(); i++) {
            String key = colliderNames.get(i);
            IntObjectPair<C> p = colliderMap.get(key);
            colliderMap.replace(key, IntObjectPair.of(p.leftInt() - 1, p.right()));
        }
    }

    public String getColliderName(int index) {
        return colliderNames.get(index);
    }

    public int getColliderIndex(String name) {
        return colliderMap.get(name).leftInt();
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean disable() {
        return disable;
    }

    private void update() {
        if (parent.positionVersion() != version[0] || parent.rotationVersion() != version[1]) {
            Vector3f position = parent.getPosition();
            Quaternionf rotation = parent.getRotation();
            rotation.transform(localPosition, globalPosition).add(position);
            rotation.mul(localRotation, globalRotation);
            version[0] = parent.positionVersion();
            version[1] = parent.rotationVersion();
            child.update();
        }
    }
}
