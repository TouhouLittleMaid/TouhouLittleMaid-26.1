package com.github.tartaricacid.touhoulittlemaid.api.event.client;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.neoforged.bus.api.Event;

import java.util.Map;

/**
 * 用于在女仆主界面额外添加一些按钮，或者添加部分渲染组件
 */
public abstract class MaidContainerGuiEvent extends Event {
    protected final AbstractMaidContainerGui<?> gui;
    protected final int leftPos;
    protected final int topPos;
    /**
     * 按钮集合，键为按钮名称，值为按钮实例
     * <p>
     * 在后面的 Render 和 Tooltip 事件中可以使用这些按钮进行额外渲染或交互
     */
    protected final Map<String, AbstractWidget> buttons;

    public MaidContainerGuiEvent(AbstractMaidContainerGui<?> gui, int leftPos, int topPos, Map<String, AbstractWidget> buttons) {
        this.gui = gui;
        this.leftPos = leftPos;
        this.topPos = topPos;
        this.buttons = buttons;
    }

    public AbstractMaidContainerGui<?> getGui() {
        return gui;
    }

    public int getLeftPos() {
        return leftPos;
    }

    public int getTopPos() {
        return topPos;
    }

    public AbstractWidget getButton(String name) {
        return buttons.get(name);
    }

    public boolean hasButton(String name) {
        return buttons.containsKey(name);
    }

    /**
     * 初始化事件，在女仆主界面按钮初始化时触发
     * <p>
     * 可以在此事件中添加自定义按钮到按钮集合中
     */
    public static class Init extends MaidContainerGuiEvent {
        public Init(AbstractMaidContainerGui<?> gui, int leftPos, int topPos, Map<String, AbstractWidget> buttons) {
            super(gui, leftPos, topPos, buttons);
        }

        /**
         * 添加一个按钮到按钮集合中，会被事件系统自动添加进按钮列表中，并渲染按钮本体
         *
         * @param name   按钮名称，必须唯一
         * @param button 按钮实例
         */
        public void addButton(String name, AbstractWidget button) {
            if (!buttons.containsKey(name)) {
                buttons.put(name, button);
            }
        }

        public void removeButton(String name) {
            buttons.remove(name);
        }
    }

    /**
     * 渲染事件，在渲染女仆主界面时触发，顺序大致在按钮本体渲染之后，文本提示渲染之前
     * <p>
     * 按钮本体会自动添加进渲染，无需手动渲染。故此事件仅用于额外渲染
     */
    public static class Render extends MaidContainerGuiEvent {
        private final GuiGraphicsExtractor graphics;
        private final int mouseX;
        private final int mouseY;
        private final float partialTicks;

        public Render(AbstractMaidContainerGui<?> gui, int leftPos, int topPos, Map<String, AbstractWidget> buttons,
                      GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
            super(gui, leftPos, topPos, buttons);
            this.graphics = graphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTicks = partialTicks;
        }

        public GuiGraphicsExtractor getGraphics() {
            return graphics;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }

    /**
     * 在最后触发，主要用于渲染文本提示
     */
    public static class Tooltip extends MaidContainerGuiEvent {
        private final GuiGraphicsExtractor graphics;
        private final int mouseX;
        private final int mouseY;
        private final float partialTicks;

        public Tooltip(AbstractMaidContainerGui<?> gui, int leftPos, int topPos, Map<String, AbstractWidget> buttons,
                       GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
            super(gui, leftPos, topPos, buttons);
            this.graphics = graphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTicks = partialTicks;
        }

        public GuiGraphicsExtractor getGraphics() {
            return graphics;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }
}
