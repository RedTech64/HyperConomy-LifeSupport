package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static regalowl.hyperconomy.Messages.*;

public class Hv {
	Hv(String args[], Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		FormatString fs = new FormatString();
		Transaction tran = hc.getTransaction();
		ETransaction ench = hc.getETransaction();
		int amount;
		try {
			ItemStack iinhand = player.getItemInHand();
			if (ench.hasenchants(iinhand) == false) {
				if (args.length == 0) {
					amount = 1;
				} else {
					amount = Integer.parseInt(args[0]);
				}
				int itd = player.getItemInHand().getTypeId();
				int da = calc.getpotionDV(player.getItemInHand());
				int newdat = calc.newData(itd, da);
				String ke = itd + ":" + newdat;
				String nam = hc.getnameData(ke);
				if (nam == null) {
					player.sendMessage(OBJECT_NOT_AVAILABLE);
				} else {
					double val = calc.getValue(nam, amount, player);
					if (calc.testId(itd) && amount > 1) {
						int numberofitem = tran.countInvitems(itd, player.getItemInHand().getData().getData(), player);
						if (amount - numberofitem > 0) {
							int addamount = amount - numberofitem;
							val = val + calc.getTvalue(nam, addamount, playerecon);
						}
					}
					double salestax = calc.getSalesTax(player, val);
					val = calc.twoDecimals(val - salestax);
					player.sendMessage(LINE_BREAK);
					//player.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " can be sold for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + val);
					player.sendMessage(fs.formatString(CAN_BE_SOLD_FOR, amount, val, nam));
					double cost = calc.getCost(nam, amount, playerecon);
					double taxpaid = calc.getPurchaseTax(nam, playerecon, cost);
					cost = calc.twoDecimals(cost + taxpaid);
					if (cost > Math.pow(10, 10)) {
						cost = -1;
					}
					double stock = 0;
					stock = sf.getStock(nam, playerecon);
					//player.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " can be purchased for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + scost);
					//player.sendMessage(ChatColor.BLUE + "The global shop currently has " + ChatColor.GREEN + "" + stock + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " available.");
					player.sendMessage(fs.formatString(CAN_BE_PURCHASED_FOR, amount, cost, nam));
					player.sendMessage(fs.formatString(GLOBAL_SHOP_CURRENTLY_HAS, stock, nam));
					player.sendMessage(LINE_BREAK);
				}
			} else {
				player.sendMessage(CANT_BUY_SELL_ENCHANTED_ITEMS);
			}
		} catch (Exception e) {
			player.sendMessage(HV_INVALID);
		}
	}
}
