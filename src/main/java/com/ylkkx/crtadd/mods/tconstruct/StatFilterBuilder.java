package com.ylkkx.crtadd.mods.tconstruct;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import c4.conarm.lib.armor.ArmorCore;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * 统计过滤器构建器，用于收集和验证装备生成的过滤条件。
 *
 * <p>支持以下过滤维度：</p>
 * <ul>
 *   <li>Material - 材料白名单/黑名单</li>
 *   <li>Modifier - 强化白名单/黑名单（支持等级约束）</li>
 *   <li>Trait - 额外注入的词条（随机选一个）</li>
 * </ul>
 *
 * <p>运算符约定：</p>
 * <ul>
 *   <li>"∈" 或 ":" - 白名单（或逻辑）</li>
 *   <li>"!∈" 或 "!:" - 黑名单（与逻辑）</li>
 * </ul>
 *
 * @author YL_kkx
 */
@ZenClass("mods.crtadd.tconstruct.StatFilterBuilder")
@ZenRegister
@ModOnly("tinkersplannerantique")
public class StatFilterBuilder {

    private static final Random RANDOM = new Random();

    /** 材料过滤条件列表。 */
    private final List<FilterCondition<String>> materialConditions = new ArrayList<>();

    /** 强化过滤条件列表。 */
    private final List<FilterCondition<ModifierFilter>> modifierConditions = new ArrayList<>();

    /** 额外词条列表（逗号分隔，随机选一个注入）。 */
    private final List<String> extraTraits = new ArrayList<>();

    /**
     * 创建新的过滤器构建器。
     */
    public StatFilterBuilder() {
    }

    /**
     * 添加材料过滤条件。
     *
     * @param op    操作符，"∈"/":" 表示白名单，"!∈"/"!:" 表示黑名单
     * @param value 逗号分隔的材料标识符列表
     */
    @ZenMethod
    public void Material(String op, String value) {
        if (op == null || value == null || value.isEmpty()) {
            return;
        }
        boolean isInclude = "∈".equals(op) || ":".equals(op);
        boolean isExclude = "!∈".equals(op) || "!:".equals(op);
        if (!isInclude && !isExclude) {
            return;
        }
        Set<String> values = parseCommaSeparated(value);
        if (values.isEmpty()) {
            return;
        }
        // 作为独立条件添加（多个 ∈ 条件之间是与逻辑，需不同部件满足）
        materialConditions.add(new FilterCondition<>(isInclude, new ArrayList<>(values)));
    }

    /**
     * 添加强化过滤条件。
     *
     * @param op    操作符，"∈"/":" 表示白名单，"!∈"/"!:" 表示黑名单
     * @param value 逗号分隔的强化定义列表，支持 "identifier" 或 "identifier.level" 格式
     */
    @ZenMethod
    public void Modifier(String op, String value) {
        if (op == null || value == null || value.isEmpty()) {
            return;
        }
        boolean isInclude = "∈".equals(op) || ":".equals(op);
        boolean isExclude = "!∈".equals(op) || "!:".equals(op);
        if (!isInclude && !isExclude) {
            return;
        }
        List<ModifierFilter> modifiers = parseModifierFilters(value);
        if (modifiers.isEmpty()) {
            return;
        }
        // 作为独立条件添加（多个 ∈ 条件之间是与逻辑）
        modifierConditions.add(new FilterCondition<>(isInclude, modifiers));
    }

    /**
     * 添加额外词条，随机选一个注入到装备中。
     *
     * @param value 逗号分隔的词条定义，格式 "traitName" 或 "traitName=color"
     */
    @ZenMethod
    public void addTrait(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        extraTraits.add(value);
    }

    /**
     * 过滤候选装备列表，根据材料和强化约束剔除不满足的装备。
     *
     * @param targets 候选装备列表
     * @return 过滤后的装备列表
     */
    public List<Item> filterTargets(List<Item> targets) {
        if (targets == null || targets.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> result = new ArrayList<>(targets);

        // 1. 验证材料标识符有效性（如有无效标识符，返回空列表）
        if (!materialConditions.isEmpty()) {
            for (FilterCondition<String> condition : materialConditions) {
                for (String id : condition.values) {
                    if (TinkerRegistry.getMaterial(id) == Material.UNKNOWN) {
                        return new ArrayList<>();
                    }
                }
            }
        }

        // 2. 材料约束过滤
        if (!materialConditions.isEmpty()) {
            result.removeIf(target -> !matchesMaterialConditions(target));
        }

        // 3. 强化约束过滤
        if (!modifierConditions.isEmpty()) {
            result.removeIf(target -> !matchesModifierConditions(target));
        }

        return result;
    }

    /**
     * 根据过滤条件构建材料列表。
     *
     * <p>构建策略：</p>
     * <ul>
     *   <li>先为每个 ∈ 条件随机分配一个部件，从该部件的白名单池中选材料</li>
     *   <li>剩余部件从完整池（仅应用黑名单）中随机选择</li>
     * </ul>
     *
     * @param target 目标装备
     * @return 材料列表
     */
    public List<Material> buildFilteredMaterials(Item target) {
        List<PartMaterialType> partTypes = getRequiredComponents(target);
        if (partTypes == null || partTypes.isEmpty()) {
            return new ArrayList<>();
        }

        int partCount = partTypes.size();

        // 1. 为每个部件获取原始可用材料池
        List<List<Material>> originalPools = new ArrayList<>();
        for (PartMaterialType partType : partTypes) {
            originalPools.add(TconStructIRandom.getUsableMaterials(partType));
        }

        // 2. 获取仅应用黑名单后的池（用于剩余部件）
        List<List<Material>> filteredPools = new ArrayList<>();
        for (List<Material> pool : originalPools) {
            filteredPools.add(applyMaterialFilterToPool(new ArrayList<>(pool)));
        }

        // 3. 收集所有 ∈ 条件
        List<Set<String>> includeConditions = new ArrayList<>();
        for (FilterCondition<String> condition : materialConditions) {
            if (condition.isInclude) {
                includeConditions.add(new HashSet<>(condition.values));
            }
        }

        // 4. 初始化结果数组
        Material[] materials = new Material[partCount];
        boolean[] assigned = new boolean[partCount];

        // 5. 为每个 ∈ 条件分配一个部件
        for (Set<String> condValues : includeConditions) {
            // 找可用该白名单材料的未分配部件
            List<Integer> candidates = new ArrayList<>();
            for (int i = 0; i < partCount; i++) {
                if (assigned[i]) {
                    continue;
                }
                for (Material mat : originalPools.get(i)) {
                    if (mat != null && condValues.contains(mat.identifier)) {
                        candidates.add(i);
                        break;
                    }
                }
            }

            if (candidates.isEmpty()) {
                // 没有未分配的部件可用，尝试所有部件（包括已分配的）
                for (int i = 0; i < partCount; i++) {
                    for (Material mat : originalPools.get(i)) {
                        if (mat != null && condValues.contains(mat.identifier)) {
                            candidates.add(i);
                            break;
                        }
                    }
                }
            }

            if (!candidates.isEmpty()) {
                int idx = candidates.get(RANDOM.nextInt(candidates.size()));
                List<Material> validMats = new ArrayList<>();
                for (Material mat : originalPools.get(idx)) {
                    if (mat != null && condValues.contains(mat.identifier)) {
                        validMats.add(mat);
                    }
                }
                if (!validMats.isEmpty()) {
                    materials[idx] = validMats.get(RANDOM.nextInt(validMats.size()));
                    assigned[idx] = true;
                }
            }
        }

        // 6. 剩余部件从过滤后的池中随机选择
        for (int i = 0; i < partCount; i++) {
            if (!assigned[i]) {
                List<Material> pool = filteredPools.get(i);
                if (pool.isEmpty()) {
                    materials[i] = Material.UNKNOWN;
                } else {
                    materials[i] = pool.get(RANDOM.nextInt(pool.size()));
                }
            }
        }

        List<Material> result = new ArrayList<>();
        for (Material mat : materials) {
            result.add(mat);
        }
        return result;
    }

    /**
     * 应用固定强化（白名单强化优先应用1个）。
     *
     * @param stack  装备栈
     * @param target 目标装备Item
     * @return 处理后的装备栈
     */
    public ItemStack applyFixedModifiers(ItemStack stack, Item target) {
        if (stack.isEmpty() || modifierConditions.isEmpty()) {
            return stack;
        }

        // 收集白名单中的强化
        List<ModifierFilter> includeFilters = new ArrayList<>();
        for (FilterCondition<ModifierFilter> condition : modifierConditions) {
            if (condition.isInclude) {
                includeFilters.addAll(condition.values);
            }
        }

        if (includeFilters.isEmpty()) {
            return stack;
        }

        Map<String, IModifier> availableMods = TconStructIRandom.getAvailableModifiersMap(target);
        int availableSlots = slimeknights.tconstruct.library.utils.ToolHelper.getFreeModifiers(stack);

        for (ModifierFilter filter : includeFilters) {
            if (availableSlots <= 0) {
                break;
            }
            IModifier mod = findMatchingModifier(availableMods, filter);
            if (mod == null) {
                continue;
            }

            // 计算需要应用的次数以达到目标等级
            int targetLevel = filter.minLevel > 0 ? filter.minLevel : 1;
            int currentLevel = getModifierLevelOnStack(stack, mod.getIdentifier());
            int needed = targetLevel - currentLevel;

            for (int i = 0; i < needed && availableSlots > 0; i++) {
                if (!TconStructIRandom.canApplyModifier(stack, mod)) {
                    break;
                }
                try {
                    mod.apply(stack);
                    stack = TconStructIRandom.rebuildStack(stack, target);
                    availableSlots = slimeknights.tconstruct.library.utils.ToolHelper.getFreeModifiers(stack);
                } catch (Exception ignored) {
                    break;
                }
            }
        }
        return stack;
    }

    /**
     * 应用随机强化（考虑过滤约束）。
     *
     * @param stack  装备栈
     * @param target 目标装备Item
     * @return 处理后的装备栈
     */
    public ItemStack addFilteredRandomModifiers(ItemStack stack, Item target) {
        if (stack.isEmpty()) {
            return stack;
        }

        int availableSlots = slimeknights.tconstruct.library.utils.ToolHelper.getFreeModifiers(stack);
        if (availableSlots <= 0) {
            return stack;
        }

        Map<String, IModifier> availableMods = TconStructIRandom.getAvailableModifiersMap(target);
        if (availableMods.isEmpty()) {
            return stack;
        }

        // 应用强化过滤约束到可用强化池
        Map<String, IModifier> filteredMods = applyModifierFilterToPool(availableMods, target);
        if (filteredMods.isEmpty()) {
            return stack;
        }

        // 记录已选择的 modifiers
        Map<String, IModifier> selectedModifiers = new LinkedHashMap<>();
        int added = 0;

        while (!stack.isEmpty() && added < availableSlots) {
            List<IModifier> options = new ArrayList<>();
            for (IModifier mod : filteredMods.values()) {
                if (TconStructIRandom.canApplyModifier(stack, mod)) {
                    options.add(mod);
                }
            }
            if (options.isEmpty()) {
                break;
            }
            IModifier modifier = options.get(RANDOM.nextInt(options.size()));
            selectedModifiers.put(modifier.getIdentifier() + "_" + added, modifier);
            added++;
            stack = TconStructIRandom.rebuildStack(stack, target);
            availableSlots = slimeknights.tconstruct.library.utils.ToolHelper.getFreeModifiers(stack);
        }

        if (!selectedModifiers.isEmpty()) {
            for (IModifier modifier : selectedModifiers.values()) {
                try {
                    if (modifier.canApply(stack.copy(), stack)) {
                        modifier.apply(stack);
                    }
                } catch (Exception ignored) {
                }
            }
            stack = TconStructIRandom.rebuildStack(stack, target);
        }
        return stack;
    }

    /**
     * 注入额外词条。
     *
     * <p>对每个 addTrait 调用：</p>
     * <ul>
     *   <li>如果值包含逗号，从中随机选一个词条</li>
     *   <li>优先通过 {@link TinkerRegistry#getTrait(String)} 获取 ITrait 并调用 apply() 正确添加</li>
     *   <li>如果 Trait 不存在，则直接追加到 Traits NBT 列表（效果可能不生效）</li>
     *   <li>已存在的词条会跳过</li>
     * </ul>
     *
     * @param stack 装备栈
     * @return 处理后的装备栈
     */
    public ItemStack applyExtraTraits(ItemStack stack) {
        if (stack.isEmpty() || extraTraits.isEmpty()) {
            return stack;
        }

        for (String traitDef : extraTraits) {
            if (traitDef == null || traitDef.isEmpty()) {
                continue;
            }

            // 逗号分隔则随机选一个
            String[] traitOptions = traitDef.split(",");
            String selected = traitOptions[RANDOM.nextInt(traitOptions.length)].trim();
            if (selected.isEmpty()) {
                continue;
            }

            stack = applySingleTrait(stack, selected);
        }
        return stack;
    }

    /**
     * 尝试添加单个词条。
     *
     * @param stack     装备栈
     * @param traitName 词条标识符
     * @return 处理后的装备栈
     */
    private ItemStack applySingleTrait(ItemStack stack, String traitName) {
        if (stack.isEmpty() || traitName.isEmpty()) {
            return stack;
        }

        // 检查是否已存在
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        if (TinkerUtil.hasTrait(root, traitName)) {
            return stack;
        }

        // 优先尝试通过 TinkerRegistry 获取并正确应用
        slimeknights.tconstruct.library.traits.ITrait trait = TinkerRegistry.getTrait(traitName);
        if (trait instanceof slimeknights.tconstruct.library.modifiers.IModifier) {
            try {
                ((slimeknights.tconstruct.library.modifiers.IModifier) trait).apply(stack);
                return stack;
            } catch (Exception ignored) {
                // 应用失败则回退到直接写入 Traits 列表
            }
        }

        // 回退：直接追加到 Traits 列表
        NBTTagList traits = TagUtil.getTraitsTagList(root);
        traits.appendTag(new NBTTagString(traitName));
        TagUtil.setTraitsTagList(root, traits);
        stack.setTagCompound(root);
        return stack;
    }

    /**
     * 检查是否有材料过滤条件。
     */
    public boolean hasMaterialConditions() {
        return !materialConditions.isEmpty();
    }

    /**
     * 检查是否有强化过滤条件。
     */
    public boolean hasModifierConditions() {
        return !modifierConditions.isEmpty();
    }

    /**
     * 检查是否有额外词条。
     */
    public boolean hasExtraTraits() {
        return !extraTraits.isEmpty();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证装备是否满足所有材料约束条件。
     *
     * <p>核心规则：</p>
     * <ul>
     *   <li>∈ 条件：至少一个部件的可用材料池包含条件中的某个材料（用于筛选装备）</li>
     *   <li>!∈ 条件：不在装备筛选阶段检查，仅在构建材料时从池中移除黑名单材料</li>
     *   <li>多个 ∈ 条件之间是与逻辑，必须由不同部件满足</li>
     * </ul>
     */
    private boolean matchesMaterialConditions(Item target) {
        List<PartMaterialType> partTypes = getRequiredComponents(target);
        if (partTypes == null || partTypes.isEmpty()) {
            return false;
        }

        // 为每个部件构建可用材料标识符集合
        List<Set<String>> partMaterialIds = new ArrayList<>();
        for (PartMaterialType partType : partTypes) {
            Set<String> ids = new HashSet<>();
            for (Material mat : TconStructIRandom.getUsableMaterials(partType)) {
                if (mat != null) {
                    ids.add(mat.identifier);
                }
            }
            partMaterialIds.add(ids);
        }

        // 收集所有 include 条件，检查是否可由不同部件满足
        List<Set<String>> includeConditions = new ArrayList<>();
        for (FilterCondition<String> condition : materialConditions) {
            if (condition.isInclude) {
                includeConditions.add(new HashSet<>(condition.values));
            }
        }

        // 检查与逻辑：每个 include 条件必须由不同部件满足
        if (!includeConditions.isEmpty()) {
            if (!canSatisfyWithDifferentParts(partMaterialIds, includeConditions)) {
                return false;
            }
        }

        // !∈ 条件不在装备筛选阶段检查（避免过度过滤）
        // 黑名单在 buildFilteredMaterials 的 applyMaterialFilterToPool 中应用

        return true;
    }

    /**
     * 检查是否可以用不同部件分别满足各个 include 条件。
     */
    private boolean canSatisfyWithDifferentParts(List<Set<String>> partMaterialIds, List<Set<String>> conditions) {
        if (conditions.size() > partMaterialIds.size()) {
            return false;
        }
        // 使用回溯法检查是否可以为每个条件分配不同的部件
        return backtrackAssign(conditions, 0, partMaterialIds, new boolean[partMaterialIds.size()]);
    }

    private boolean backtrackAssign(List<Set<String>> conditions, int condIdx,
                                    List<Set<String>> partMaterialIds, boolean[] used) {
        if (condIdx >= conditions.size()) {
            return true;
        }
        Set<String> condValues = conditions.get(condIdx);
        for (int i = 0; i < partMaterialIds.size(); i++) {
            if (used[i]) {
                continue;
            }
            Set<String> partIds = partMaterialIds.get(i);
            boolean canSatisfy = false;
            for (String val : condValues) {
                if (partIds.contains(val)) {
                    canSatisfy = true;
                    break;
                }
            }
            if (canSatisfy) {
                used[i] = true;
                if (backtrackAssign(conditions, condIdx + 1, partMaterialIds, used)) {
                    return true;
                }
                used[i] = false;
            }
        }
        return false;
    }

    /**
     * 验证装备是否满足所有强化约束条件。
     */
    private boolean matchesModifierConditions(Item target) {
        Map<String, IModifier> availableMods = TconStructIRandom.getAvailableModifiersMap(target);

        for (FilterCondition<ModifierFilter> condition : modifierConditions) {
            boolean conditionSatisfied = false;
            for (ModifierFilter filter : condition.values) {
                boolean filterMatched = false;
                for (IModifier mod : availableMods.values()) {
                    if (matchesModifierFilter(mod, filter)) {
                        filterMatched = true;
                        break;
                    }
                }
                if (filterMatched) {
                    conditionSatisfied = true;
                    break;
                }
            }
            if (condition.isInclude) {
                if (!conditionSatisfied) {
                    return false;
                }
            } else {
                if (conditionSatisfied) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查 modifier 是否匹配过滤条件。
     */
    private boolean matchesModifierFilter(IModifier mod, ModifierFilter filter) {
        if (mod == null) {
            return false;
        }
        String id = mod.getIdentifier();
        if (!id.equals(filter.identifier)) {
            return false;
        }
        if (filter.minLevel < 0) {
            return true; // 任意等级
        }
        // 获取 modifier 当前等级（从 NBT 读取）
        // 对于可用 modifier 池中的 modifier，等级通常为 1
        // 这里简化处理：只检查 identifier 匹配，等级约束在应用时检查
        return true;
    }

    /**
     * 对材料池应用过滤约束。
     *
     * <p>处理逻辑：</p>
     * <ul>
     *   <li>黑名单：从每个部件的池中移除黑名单材料</li>
     *   <li>白名单：不在此处限制，白名单约束仅在装备筛选阶段使用（确保装备能满足"至少一个部件是X"）</li>
     * </ul>
     */
    private List<Material> applyMaterialFilterToPool(List<Material> pool) {
        if (pool == null || pool.isEmpty()) {
            return new ArrayList<>();
        }
        List<Material> result = new ArrayList<>(pool);

        // 仅应用黑名单：所有部件都不能包含黑名单材料
        for (FilterCondition<String> condition : materialConditions) {
            if (!condition.isInclude) {
                result.removeIf(mat -> mat != null && condition.values.contains(mat.identifier));
            }
        }

        return result;
    }

    /**
     * 对强化池应用过滤约束。
     */
    private Map<String, IModifier> applyModifierFilterToPool(Map<String, IModifier> pool, Item target) {
        if (pool == null || pool.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, IModifier> result = new LinkedHashMap<>(pool);

        // 应用黑名单
        for (FilterCondition<ModifierFilter> condition : modifierConditions) {
            if (!condition.isInclude) {
                for (ModifierFilter filter : condition.values) {
                    result.entrySet().removeIf(entry -> {
                        IModifier mod = entry.getValue();
                        if (mod == null) {
                            return false;
                        }
                        if (!mod.getIdentifier().equals(filter.identifier)) {
                            return false;
                        }
                        if (filter.minLevel < 0) {
                            return true; // 任意等级都排除
                        }
                        // 等级检查：获取当前 modifier 等级
                        int currentLevel = getModifierLevel(mod);
                        return currentLevel >= filter.minLevel;
                    });
                }
            }
        }

        return result;
    }

    /**
     * 在可用强化中查找匹配过滤条件的强化。
     */
    private IModifier findMatchingModifier(Map<String, IModifier> availableMods, ModifierFilter filter) {
        for (IModifier mod : availableMods.values()) {
            if (mod != null && mod.getIdentifier().equals(filter.identifier)) {
                return mod;
            }
        }
        return null;
    }

    /**
     * 获取 modifier 的等级（简化实现，实际从 NBT 读取）。
     */
    private int getModifierLevel(IModifier mod) {
        // 对于未应用的 modifier，默认等级为 1
        return 1;
    }

    /**
     * 获取装备栈上指定 modifier 的当前等级。
     *
     * @param stack      装备栈
     * @param identifier modifier 标识符
     * @return 当前等级，未找到返回 0
     */
    private int getModifierLevelOnStack(ItemStack stack, String identifier) {
        if (stack.isEmpty() || identifier == null || identifier.isEmpty()) {
            return 0;
        }
        try {
            NBTTagCompound modifierTag = TinkerUtil.getModifierTag(stack, identifier);
            if (modifierTag == null) {
                return 0;
            }
            slimeknights.tconstruct.library.modifiers.ModifierNBT data = slimeknights.tconstruct.library.modifiers.ModifierNBT.readTag(modifierTag);
            return data != null ? data.level : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取装备所需部件列表。
     */
    private List<PartMaterialType> getRequiredComponents(Item target) {
        if (target instanceof ToolCore) {
            return ((ToolCore) target).getRequiredComponents();
        } else if (target instanceof ArmorCore) {
            return ((ArmorCore) target).getRequiredComponents();
        }
        return null;
    }

    /**
     * 按逗号分割字符串并去重。
     */
    private Set<String> parseCommaSeparated(String value) {
        Set<String> result = new HashSet<>();
        for (String s : value.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * 解析强化过滤定义。
     *
     * <p>支持格式：</p>
     * <ul>
     *   <li>"fiery" - 任意等级</li>
     *   <li>"fiery.3" - 等级 ≥ 3</li>
     * </ul>
     */
    private List<ModifierFilter> parseModifierFilters(String value) {
        List<ModifierFilter> result = new ArrayList<>();
        for (String s : value.split(",")) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int dotIdx = trimmed.lastIndexOf('.');
            if (dotIdx > 0) {
                String id = trimmed.substring(0, dotIdx);
                try {
                    int level = Integer.parseInt(trimmed.substring(dotIdx + 1));
                    result.add(new ModifierFilter(id, level));
                } catch (NumberFormatException e) {
                    // 解析失败，视为无等级要求
                    result.add(new ModifierFilter(trimmed, -1));
                }
            } else {
                result.add(new ModifierFilter(trimmed, -1));
            }
        }
        return result;
    }

    /**
     * 合并或添加过滤条件（同类型按 OR 合并）。
     */
    private <T> void mergeOrAdd(List<FilterCondition<T>> conditions, FilterCondition<T> newCondition) {
        for (FilterCondition<T> existing : conditions) {
            if (existing.isInclude == newCondition.isInclude) {
                // 同类型合并
                Set<T> merged = new HashSet<>(existing.values);
                merged.addAll(newCondition.values);
                existing.values = new ArrayList<>(merged);
                return;
            }
        }
        conditions.add(newCondition);
    }

    // ==================== 内部类 ====================

    /**
     * 过滤条件封装。
     *
     * @param <T> 条件值类型
     */
    private static class FilterCondition<T> {
        boolean isInclude;
        List<T> values;

        FilterCondition(boolean isInclude, List<T> values) {
            this.isInclude = isInclude;
            this.values = values;
        }
    }

    /**
     * 强化过滤定义。
     */
    private static class ModifierFilter {
        String identifier;
        int minLevel; // -1 表示任意等级

        ModifierFilter(String identifier, int minLevel) {
            this.identifier = identifier;
            this.minLevel = minLevel;
        }
    }
}
