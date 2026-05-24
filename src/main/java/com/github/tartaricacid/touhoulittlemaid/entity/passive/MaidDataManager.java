package com.github.tartaricacid.touhoulittlemaid.entity.passive;

public class MaidDataManager {

    private final EntityMaid maid;

    public MaidDataManager(EntityMaid entityMaid) {
        maid = entityMaid;
    }

    public interface View {

        MaidDataManager getDataManager();

    }

}
