package com.github.tartaricacid.touhoulittlemaid.api.client.render;

public enum MaidRenderState {
    /**
     * 普通实体渲染状态，默认情况
     */
    ENTITY,
    /**
     * 雕像，方块形态的
     */
    STATUE,
    /**
     * 手办，方块形态
     */
    GARAGE_KIT,
    /**
     * 手办，物品形态
     */
    GARAGE_KIT_ITEM,
    /**
     * GUI 临时预览
     */
    GUI
}
