package com.github.tartaricacid.touhoulittlemaid.entity.passive;

public class MaidModelView {

    private final EntityMaid maid;

    public MaidModelView(EntityMaid entityMaid) {
        maid = entityMaid;
    }

    public interface View {

        MaidModelView getModelView();

    }

}
