package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register.builder;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register.task.*;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public class MaidTaskBuilder {
    private final List<IMaidTask> tasks = Lists.newArrayList();

    @Info(value = """
            Creates a new blank task builder. It does not contain any Brain content, you need to add Brain using java.forClass. <br>
            创建一个新的空白的任务构建器。里面没有 Brain 内容，需要自行使用 java.forClass 来添加 Brain。
            """, params = {
            @Param(name = "id", value = """
                    The unique identifier for the task. <br>
                    任务的 ID
                    """),
            @Param(name = "icon", value = """
                    use an item to represent the task icon <br>
                    用物品来表示任务的图标
                    """)
    })
    public BaseTaskJS.Builder baseTask(ResourceLocation id, ItemStack icon) {
        BaseTaskJS.Builder builder = new BaseTaskJS.Builder(id, icon);
        tasks.add(new BaseTaskJS(builder));
        return builder;
    }

    @Info(value = """
            Creates a new melee task builder, you generally only need to modify weapon checks, extra attack, etc. <br>
            创建一个新的近战任务构建器，你一般只需要修改武器判断、额外攻击等内容即可
            """, params = {
            @Param(name = "id", value = """
                    The unique identifier for the task. <br>
                    任务的 ID
                    """),
            @Param(name = "icon", value = """
                    use an item to represent the task icon <br>
                    用物品来表示任务的图标
                    """)
    })
    public MeleeTaskJS.Builder meleeTask(ResourceLocation id, ItemStack icon) {
        MeleeTaskJS.Builder builder = new MeleeTaskJS.Builder(id, icon);
        tasks.add(new MeleeTaskJS(builder));
        return builder;
    }

    @Info(value = """
            Creates a new ranged attack task builder, you generally only need to modify weapon checks, add custom ranged attack logic, etc. <br>
            创建一个新的远程攻击任务构建器，你一般只需要修改武器判断，添加自定义的远程攻击逻辑等内容即可
            """, params = {
            @Param(name = "id", value = """
                    The unique identifier for the task. <br>
                    任务的 ID
                    """),
            @Param(name = "icon", value = """
                    use an item to represent the task icon <br>
                    用物品来表示任务的图标
                    """)
    })
    public RangedAttackTaskJS.Builder rangedAttackTask(ResourceLocation id, ItemStack icon) {
        RangedAttackTaskJS.Builder builder = new RangedAttackTaskJS.Builder(id, icon);
        tasks.add(new RangedAttackTaskJS(builder));
        return builder;
    }

    @Info(value = """
            Creates a new farm task builder, you need to add farming logic such as planting, harvesting, and other content. <br>
            创建一个新的种田任务构建器，你需要添加种田的逻辑，比如种植、收割等诸多内容
            """, params = {
            @Param(name = "id", value = """
                    The unique identifier for the task. <br>
                    任务的 ID
                    """),
            @Param(name = "icon", value = """
                    use an item to represent the task icon <br>
                    用物品来表示任务的图标
                    """)
    })
    public FarmTaskJS.Builder farmTask(ResourceLocation id, ItemStack icon) {
        FarmTaskJS.Builder builder = new FarmTaskJS.Builder(id, icon);
        tasks.add(new FarmTaskJS(builder));
        return builder;
    }

    @Info(value = """
            Creates a new walk to block task builder, you need to add logic such as start condition, searching for blocks, arriving at blocks, etc. <br>
            创建一个走向目标方块的任务构建器，你需要定义起始条件、方块搜索判断、到达目标方块后的行为等内容。
            """, params = {
            @Param(name = "id", value = """
                    The unique identifier for the task. <br>
                    任务的 ID
                    """),
            @Param(name = "icon", value = """
                    use an item to represent the task icon <br>
                    用物品来表示任务的图标
                    """)
    })
    public WalkToBlockTaskJS.Builder walkToBlockTask(ResourceLocation id, ItemStack icon) {
        WalkToBlockTaskJS.Builder builder = new WalkToBlockTaskJS.Builder(id, icon);
        tasks.add(new WalkToBlockTaskJS(builder));
        return builder;
    }

    @Info(value = """
            Creates a new walk to living entity task builder, you need to add logic such as start condition, searching for entities, arriving at entities, etc. <br>
            创建一个走向目标生物的任务构建器，你需要定义起始条件、实体搜索判断、到达目标实体后的行为等内容。
            """, params = {
            @Param(name = "id", value = """
                    The unique identifier for the task. <br>
                    任务的 ID
                    """),
            @Param(name = "icon", value = """
                    use an item to represent the task icon <br>
                    用物品来表示任务的图标
                    """)
    })
    public WalkToLivingEntityTaskJS.Builder walkToLivingEntityTask(ResourceLocation id, ItemStack icon) {
        WalkToLivingEntityTaskJS.Builder builder = new WalkToLivingEntityTaskJS.Builder(id, icon);
        tasks.add(new WalkToLivingEntityTaskJS(builder));
        return builder;
    }

    @HideFromJS
    public void register(TaskManager manager) {
        Set<ResourceLocation> existingTasks = Sets.newHashSet();
        for (IMaidTask task : this.tasks) {
            ResourceLocation uid = task.getUid();
            if (!existingTasks.contains(uid)) {
                existingTasks.add(uid);
                manager.add(task);
            }
        }
        this.tasks.clear();
    }
}
