package com.chai.inv.test;


public class Main {
	public static void main(String[] args) {
		String s="2.4";
		int a=(int)Double.parseDouble(s);
		System.out.println(a);
//String s[]={"LGA_ID",
//		"WAREHOUSE_CODE",
//		"ITEM_ID",
//		"ITEM_NUMBER",
//		"PHYSICAL_COUNT_DATE",
//		"REASON",
//		"STOCK_BALANCE",
//		"PHYSICAL_STOCK_COUNT",
//		"DIFFERENCE"
//		};
//
//
//		
////		for (String string : s) {
////			if(string.contains("ID") || string.contains("BY") || string.contains("NUMBER")){
////				System.out.println("@Column(name="+"  "+string+") private Integer x_"+string+";");
////			}else if(string.contains("DATE") || string.contains("ON")){
////				System.out.println("@Column(name="+string+") private Date x_"+string+";");
////			}else {
////				System.out.println("@Column(name="+string+") private String x_"+string+";");
////			}
////		}{field:'EMAIL',title:'EMAIL',sortable:true}
//for (String string : s) {
//	System.out.println("{field:'"+string+"',title:'"+string+"',sortable:true},");
//}
	}
}