package com.ylkkx.crtadd.mods.tconstruct;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.library.utils.ToolHelper;
import c4.conarm.lib.armor.ArmorCore;
import c4.conarm.lib.ArmoryRegistry;
import c4.conarm.lib.tinkering.ArmorBuilder;
import c4.conarm.lib.tinkering.TinkersArmor;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.crtadd.tconstruct.TconStructIRandom")
@ZenRegister
@ModOnly("tinkersplannerantique")
public class TconStructIRandom {
    private static final Random RANDOM = new Random();

    @ZenMethod
    public static IItemStack Random(String type) {
        List<Item> targets = getTargets(type);
        if (targets.isEmpty()) {
            return null;
        }
        Item target = targets.get(RANDOM.nextInt(targets.size()));
        List<Material> materials = buildRandomMaterials(target);
        ItemStack result = buildItem(target, materials);
        if (result.isEmpty()) {
            return null;
        }
        // 随机添加 modifiers
        result = addRandomModifiers(result, target);
        if (result.isEmpty()) {
            return null;
        }
        return CraftTweakerMC.getIItemStack(result);
    }

    private static List<Item> getTargets(String type) {
        List<Item> result = new ArrayList<>();
        if ("all".equalsIgnoreCase(type)) {
            result.addAll(getToolTargets());
            result.addAll(getArmorTargets());
        } else if ("tool".equalsIgnoreCase(type)) {
            result.addAll(getToolTargets());
        } else if ("armor".equalsIgnoreCase(type)) {
            result.addAll(getArmorTargets());
        } else {
            // 根据工具ID查找，支持忽略大小写
            result.addAll(getTargetsById(type));
        }
        return result;
    }

    /**
     * 根据工具ID查找目标（忽略大小写），参考 PlannerRegistryJsonExporter.registryName()
     * 支持带命名空间的ID如 "tconstruct:rapier" 或纯ID如 "rapier"
     */
    private static List<Item> getTargetsById(String id) {
        List<Item> result = new ArrayList<>();
        if (id == null || id.isEmpty()) {
            return result;
        }
        String searchId = id.toLowerCase(java.util.Locale.ROOT);
        for (Item item : Item.REGISTRY) {
            if (item == null) {
                continue;
            }
            ResourceLocation registryName = item.getRegistryName();
            if (registryName == null) {
                continue;
            }
            String itemId = registryName.toString().toLowerCase(java.util.Locale.ROOT);
            // 完全匹配或纯ID部分匹配
            if (itemId.equals(searchId) || itemId.endsWith(":" + searchId) || itemId.equals(searchId.replace(":", ""))) {
                if (item instanceof ToolCore || item instanceof ArmorCore) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    private static List<Item> getToolTargets() {
        List<Item> tools = new ArrayList<>();
        LinkedHashSet<ToolCore> toolSet = new LinkedHashSet<>();
        toolSet.addAll(TinkerRegistry.getToolStationCrafting());
        toolSet.addAll(TinkerRegistry.getToolForgeCrafting());
        for (ToolCore tool : toolSet) {
            tools.add(tool);
        }
        return tools;
    }

    private static List<Item> getArmorTargets() {
        List<Item> armors = new ArrayList<>();
        for (ArmorCore armor : ArmoryRegistry.getArmorCrafting()) {
            armors.add(armor);
        }
        return armors;
    }

    private static List<Material> buildRandomMaterials(Item target) {
        List<PartMaterialType> partTypes;
        if (target instanceof ToolCore) {
            partTypes = ((ToolCore) target).getRequiredComponents();
        } else if (target instanceof ArmorCore) {
            partTypes = ((ArmorCore) target).getRequiredComponents();
        } else {
            return new ArrayList<>();
        }
        List<Material> materials = new ArrayList<>();
        for (PartMaterialType partType : partTypes) {
            materials.add(getRandomMaterial(partType));
        }
        return materials;
    }

    /**
     * 从可用材料中随机选择材料，参考 PlannerScreen.getUsableMaterials()
     */
    private static Material getRandomMaterial(PartMaterialType partType) {
        List<Material> validMaterials = getUsableMaterials(partType);
        if (validMaterials.isEmpty()) {
            return Material.UNKNOWN;
        }
        return validMaterials.get(RANDOM.nextInt(validMaterials.size()));
    }

    /**
     * 获取可用于指定零件类型的材料列表，参考 PlannerScreen.getUsableMaterials()
     */
    private static List<Material> getUsableMaterials(PartMaterialType partType) {
        List<Material> materials = new ArrayList<>();
        for (Material material : TinkerRegistry.getAllMaterials()) {
            if (partType != null && partType.isValidMaterial(material)) {
                materials.add(material);
            }
        }
        if (materials.isEmpty()) {
            IToolPart part = getDisplayPart(partType);
            if (part != null) {
                for (Material material : TinkerRegistry.getAllMaterials()) {
                    if (material != null && material != Material.UNKNOWN && part.canUseMaterialForRendering(material)) {
                        materials.add(material);
                    }
                }
            }
        }
        return materials;
    }

    private static IToolPart getDisplayPart(PartMaterialType partType) {
        for (IToolPart part : partType.getPossibleParts()) {
            return part;
        }
        for (Item item : Item.REGISTRY) {
            if (item instanceof IToolPart && partType.isValidItem((IToolPart) item)) {
                return (IToolPart) item;
            }
        }
        return null;
    }

    private static ItemStack buildItem(Item target, List<Material> materials) {
        if (target instanceof ToolCore) {
            return ((ToolCore) target).buildItem(materials);
        } else if (target instanceof ArmorCore) {
            return ((ArmorCore) target).buildItem(materials);
        }
        return ItemStack.EMPTY;
    }

    /**
     * 随机添加 modifiers，参考 PlannerScreen.randomize() 和 PlannerBlueprint
     *
     * 关键区别于之前版本：
     * - 使用 LinkedHashMap 而非 List 来去重，同一 identifier 可添加多次
     * - 每次添加后调用 rebuildStack() 更新 availableSlots
     * - 最后遍历所有 identifiers 调用 modifier.apply()
     */
    private static ItemStack addRandomModifiers(ItemStack stack, Item target) {
        if (stack.isEmpty()) {
            return stack;
        }
        // 获取可用 modifier 槽位数
        int availableSlots = ToolHelper.getFreeModifiers(stack);
        if (availableSlots <= 0) {
            return stack;
        }
        // 获取可用的 modifiers
        Map<String, IModifier> availableModifiersMap = getAvailableModifiersMap(target);
        if (availableModifiersMap.isEmpty()) {
            return stack;
        }
        // 记录已添加的 modifiers（LinkedHashMap 的 values 可以有重复）
        Map<String, IModifier> selectedModifiers = new LinkedHashMap<>();
        int added = 0;
        while (!stack.isEmpty() && added < availableSlots) {
            // 过滤出可以添加到当前工具的 modifiers
            List<IModifier> options = new ArrayList<>();
            for (IModifier mod : availableModifiersMap.values()) {
                if (canApplyModifier(stack, mod)) {
                    options.add(mod);
                }
            }
            if (options.isEmpty()) {
                break;
            }
            IModifier modifier = options.get(RANDOM.nextInt(options.size()));
            // 添加到 selectedModifiers（LinkedHashMap 支持重复 key）
            selectedModifiers.put(modifier.getIdentifier() + "_" + added, modifier);
            added++;
            // 重建工具以更新 availableSlots（参考 PlannerBlueprint 每次添加后 rebuild）
            stack = rebuildStack(stack, target);
        }
        // 如果有添加的 modifiers，应用它们（参考 PlannerBlueprint.createPreview() 第98-112行）
        if (!selectedModifiers.isEmpty()) {
            for (IModifier modifier : selectedModifiers.values()) {
                try {
                    if (modifier.canApply(stack.copy(), stack)) {
                        modifier.apply(stack);
                    }
                } catch (TinkerGuiException e) {
                    // 忽略
                } catch (Exception e) {
                    // 忽略
                }
            }
            // 最后重建工具（参考 PlannerBlueprint.rebuildPreview()）
            stack = rebuildStack(stack, target);
        }
        return stack;
    }

    /**
     * 获取目标可用的 modifiers 列表（使用 Map 去重），参考：
     * - ToolPlannerTarget.getAvailableModifiers()
     * - ConArmClientCompat.ArmorPlannerTarget.getAvailableModifiers()
     */
    private static Map<String, IModifier> getAvailableModifiersMap(Item target) {
        Map<String, IModifier> modifiers = new LinkedHashMap<>();
        if (target instanceof ToolCore) {
            for (IModifier modifier : TinkerRegistry.getAllModifiers()) {
                if (modifier != null && modifier.hasItemsToApplyWith() && !modifier.isHidden()) {
                    // 过滤特殊 modifiers (参考 PlannerScreen.getDisplayModifiers)
                    if (!isFilteredModifier(modifier)) {
                        modifiers.putIfAbsent(modifier.getIdentifier(), modifier);
                    }
                }
            }
        } else if (target instanceof ArmorCore) {
            // 盔甲 modifiers 使用更复杂的过滤逻辑，参考 ConArmClientCompat.ArmorPlannerTarget.hasApplicationItems()
            for (IModifier modifier : ArmoryRegistry.getAllArmorModifiers()) {
                if (modifier != null && !modifier.isHidden() && hasArmorApplicationItems(modifier)) {
                    if (!isFilteredModifier(modifier)) {
                        modifiers.putIfAbsent(modifier.getIdentifier(), modifier);
                    }
                }
            }
        }
        return modifiers;
    }

    /**
     * 检查 armor modifier 是否有可应用的物品，参考 ConArmClientCompat.ArmorPlannerTarget.hasApplicationItems()
     */
    private static boolean hasArmorApplicationItems(IModifier modifier) {
        String className = modifier.getClass().getName().toLowerCase(java.util.Locale.ROOT);
        // modpolished 和 modextraarmortrait 类（排除 display 版本）
        if (className.contains("modpolished") && !className.contains("display")) {
            return true;
        }
        if (className.contains("modextraarmortrait") && !className.contains("display")) {
            return true;
        }
        // 标准检查
        if (modifier.hasItemsToApplyWith()) {
            return true;
        }
        // ConArm 特殊 modifiers - 优先使用 ConArmCompat.getModifierItems()
        List<List<ItemStack>> items = getConArmorModifierItems(modifier);
        if (items != null) {
            for (List<ItemStack> group : items) {
                if (group != null && !group.isEmpty()) {
                    return true;
                }
            }
        }
        // 备用：检查 ToolModifier 和 ModifierTrait 类型（参考 ArmorPlannerTarget.getToolModifierItems()）
        if (modifier instanceof slimeknights.tconstruct.tools.modifiers.ToolModifier) {
            return true;
        }
        if (modifier instanceof slimeknights.tconstruct.library.modifiers.ModifierTrait) {
            return true;
        }
        return false;
    }

    /**
     * 获取 ConArm modifier 的应用物品列表，参考 ConArmCompat.getModifierItems()
     */
    private static List<List<ItemStack>> getConArmorModifierItems(IModifier modifier) {
        // 优先尝试获取 ConArm 的 armor modifier items
        if (modifier instanceof c4.conarm.lib.modifiers.ArmorModifier) {
            return ((c4.conarm.lib.modifiers.ArmorModifier) modifier).getItems();
        }
        if (modifier instanceof c4.conarm.lib.modifiers.ArmorModifierTrait) {
            return ((c4.conarm.lib.modifiers.ArmorModifierTrait) modifier).getItems();
        }
        return null;
    }

    /**
     * 检查是否是被过滤的特殊 modifier，参考 PlannerScreen.getDisplayModifiers()
     */
    private static boolean isFilteredModifier(IModifier modifier) {
        String idLower = modifier.getIdentifier().toLowerCase(java.util.Locale.ROOT);
        String classLower = modifier.getClass().getName().toLowerCase(java.util.Locale.ROOT);
        String name = modifier.getLocalizedName();
        String nameLower = name != null ? name.toLowerCase(java.util.Locale.ROOT) : "";

        // ID 过滤
        if (idLower.contains("extratrait") || idLower.contains("emboss") || idLower.contains("fortify")
                || idLower.contains("material") || idLower.contains("polished")) {
            return true;
        }
        // Class 过滤
        if (classLower.contains("extratrait") || classLower.contains("fortifydisplay") || classLower.contains("modfortify")
                || classLower.contains("modpolished")) {
            return true;
        }
        // 名称过滤（中文 + 英文），参考 PlannerScreen.getDisplayModifiers() 第922行
        if (name.contains("刻印") || nameLower.contains("emboss")
                || name.startsWith("强化 (") || name.startsWith("强化（")
                || nameLower.startsWith("strengthened (") || nameLower.startsWith("fortified")
                || name.startsWith("研磨 (") || name.startsWith("研磨（") || nameLower.startsWith("polished")) {
            return true;
        }
        return false;
    }

    /**
     * 检查 modifier 是否可以应用到当前工具
     */
    private static boolean canApplyModifier(ItemStack stack, IModifier modifier) {
        if (stack.isEmpty() || modifier == null) {
            return false;
        }
        try {
            return modifier.canApply(stack.copy(), stack);
        } catch (TinkerGuiException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 重建 ItemStack，参考 PlannerBlueprint.rebuildPreview()
     */
    private static ItemStack rebuildStack(ItemStack stack, Item target) {
        if (stack.isEmpty()) {
            return stack;
        }
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        try {
            if (target instanceof TinkersItem) {
                ToolBuilder.rebuildTool(root, (TinkersItem) target);
            } else if (target instanceof TinkersArmor) {
                ArmorBuilder.rebuildArmor(root, (TinkersArmor) target);
            }
            stack.setTagCompound(root);
        } catch (TinkerGuiException ignored) {
        } catch (Exception ignored) {
        }
        return stack;
    }
}