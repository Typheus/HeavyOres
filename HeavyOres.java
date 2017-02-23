package typheus.HeavyOres;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.sun.xml.internal.fastinfoset.sax.Properties;
import com.typesafe.config.Config;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = "HeavyOres", name = "Heavy Ores", version = "1.0")
public class HeavyOres {
		//TODO Auto-generated method stub
	
	public static Configuration heavyOresConfig;
	public static ArrayList<String> listOfAffectedOres;
	public static ArrayList<String> listOfExemptOres; 
	public static ArrayList<String> listOfExemptGroups; 
	public static int oreStackSizeLimit;
	public static boolean allOresAffected;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//Item/Block init and registering
		//Config handling
		
		heavyOresConfig = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//Prox, TileEntity, entity, GUI and Packet Registering
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ChangeOreStackSizes(listOfAffectedOres);
		if (allOresAffected){
			ChangeAllOresStackSizes();
		}
		
	}
	
	public static void syncConfig() {
		try {
			
			heavyOresConfig.load();
			
			Property stackSize = heavyOresConfig.get(Configuration.CATEGORY_GENERAL,
					"oreStackSizeLimit",
					4,
					"The stack size you wish to to restrict the ores to.");
			
			Property allBasicOresAffected = heavyOresConfig.get(Configuration.CATEGORY_GENERAL,
					"allOresAffected",
					"true",
					"Whether all ore dictionary names containing \"ore\" are affected.");
			
			Property allAffectedOres = heavyOresConfig.get("ore_lists", 
					"oresWithLimitedStacks",
					new String[]{"oreIron","oreGold","oreCopper","oreTin","oreLead","oreSilver"},
					"If you want a specific set of ores to be affected then enter them here using their ore dictionary names.");
			
			Property exemptOres = heavyOresConfig.get("ore_lists",
					"oresExemptFromRule",
					new String[]{""},
					"If \"allOresAffected\" is set to true, then this will decide what ores are not affected. Can also be used to fix non-ores being affected. Overrides affected ore list.");
			
			Property exemptGroups = heavyOresConfig.get("ore_lists",
					"exemptCollections",
					new String[]{""},
					"This can exempt collections of items or ore. If the ore dictionary contains the entered text, it will be exempt. Overrides affected ore list.");
			
			oreStackSizeLimit = stackSize.getInt();
			listOfAffectedOres = new ArrayList<String>(Arrays.asList(allAffectedOres.getStringList()));
			allOresAffected = allBasicOresAffected.getBoolean();
			listOfExemptOres = new ArrayList<String>(Arrays.asList(exemptOres.getStringList()));
			listOfExemptGroups = new ArrayList<String>(Arrays.asList(exemptGroups.getStringList()));
			
		} catch (Exception Q) {
			
		} finally {
			if (heavyOresConfig.hasChanged()) {
				heavyOresConfig.save();
			}
		}
	}
	
	//Used if "allOresAffected" in config file is set to true
	public void ChangeAllOresStackSizes(){
		for (String oreName : OreDictionary.getOreNames()){
			if (oreName.contains("ore") && !listOfExemptOres.contains(oreName) && !ContainsExemptGroup(oreName)){
				ChangeItemStackMaxSizes(OreDictionary.getOres(oreName), oreStackSizeLimit);
			}
		}
	}
	
	//Used to change a specific set of ores described in "oresWithLimitedStacks" in config file.
	public void ChangeOreStackSizes(ArrayList<String> oreDictionaryNames){
		for (String oreName : oreDictionaryNames){
			
			if (!listOfExemptOres.contains(oreName) && !ContainsExemptGroup(oreName)){
				ChangeItemStackMaxSizes(OreDictionary.getOres(oreName), oreStackSizeLimit);
			}
			
		}
	}
	
	//Performs the stack size change on everything contained in the item stack list so long as it's stackable.
	public void ChangeItemStackMaxSizes(ArrayList<ItemStack>oreItemStacks, int stackSize){
		for (ItemStack oreStack: oreItemStacks){
			if (oreStack.isStackable()){
				oreStack.getItem().setMaxStackSize(stackSize);
			}
		}
	}
	
	//Used to compare the ore name to the entries in the exempt groups list.
	public boolean ContainsExemptGroup(String oreName){
		for (String group : listOfExemptGroups){
			if (oreName.contains(group)) return true;
		}
		return false;
	}
	

}
