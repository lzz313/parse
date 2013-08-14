package org.test.parse;

import java.math.BigDecimal;


/**
 * 经纬度格式转换
 * 
 * 031°22′17.1″北  <----> -31.37142
 * 120°44′01.7″东  <----> 120.73380
 * 
 * @author luzz
 *
 */
public class LRConverter {

	public static String toDMS(BigDecimal deg, String format, int dp) {
		if (dp == -1) {
			if ("d".equalsIgnoreCase(format)) {
				dp = 4;
			} else if ("dm".equalsIgnoreCase(format)) {
				dp = 2;
			} else if ("dms".equalsIgnoreCase(format)) {
				dp = 0;
			} else {
				format = "dms";
				dp = 0;
			}
		}
		deg = deg.abs();
		BigDecimal d;
		String dStr = "", mStr = "", sStr = "";
		if ("d".equalsIgnoreCase(format)) {
			d = deg.setScale(dp);
			dStr = d.toString();
			if (d.compareTo(new BigDecimal(100)) < 0) {
				dStr = "0" + dStr;
			} else if (d.compareTo(new BigDecimal(10)) < 0) {
				dStr = "0" + dStr;
			}

			dStr = dStr + "\u00B0";
		} else if ("dm".equalsIgnoreCase(format)) {
			BigDecimal min = deg.multiply(new BigDecimal(60)).setScale(dp);
			BigDecimal dTemp = min.divide(new BigDecimal(60));
			BigDecimal mTemp = min.remainder(new BigDecimal(60)).setScale(dp);

			if (dTemp.compareTo(new BigDecimal(100)) < 0) {
				dStr = "0" + dTemp.intValue();
			} else if (dTemp.compareTo(new BigDecimal(10)) < 0) {
				dStr = "0" + dTemp.intValue();
			} else {
				dStr = "" + dTemp.intValue();
			}

			if (mTemp.compareTo(new BigDecimal(10)) < 0) {
				mStr = "0" + mTemp.intValue();
			} else {
				mStr = "" + mTemp.intValue();
			}

			dStr = dStr + "\u00B0" + mStr + "\u2032";
		} else if ("dms".equalsIgnoreCase(format)) {
			BigDecimal sec = deg.multiply(new BigDecimal(3600)).setScale(dp,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal dTemp = sec.divide(new BigDecimal(3600), dp,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal mTemp = sec.divide(new BigDecimal(60), dp,
					BigDecimal.ROUND_HALF_UP).remainder(new BigDecimal(60));
			BigDecimal sTemp = sec.remainder(new BigDecimal(60)).setScale(dp,
					BigDecimal.ROUND_HALF_UP);

			if (dTemp.compareTo(new BigDecimal(100)) < 0) {
				dStr = "0" + dTemp.intValue();
			} else if (dTemp.compareTo(new BigDecimal(10)) < 0) {
				dStr = "0" + dTemp.intValue();
			} else {
				dStr = "" + dTemp.intValue();
			}

			if (mTemp.compareTo(new BigDecimal(10)) < 0) {
				mStr = "0" + mTemp.intValue();
			} else {
				mStr = "" + mTemp.intValue();
			}

			if (sTemp.compareTo(new BigDecimal(10)) < 0) {
				sStr = "0" + sTemp.toString();
			} else {
				sStr = "" + sTemp.toString();
			}

			dStr = dStr + '\u00B0' + mStr + '\u2032' + sStr + '\u2033';
		}

		return dStr;
	}

	public static String toLat(String deg, String format, int dp) {
		BigDecimal bDeg = new BigDecimal(deg);
		String lat = toDMS(bDeg, format, dp);

		if ("".equals(lat)) {
			return "-";
		} else {
			return lat + ((bDeg.compareTo(BigDecimal.ZERO) < 0) ? '南' : '北');
		}
	}

	public static String toLon(String deg, String format, int dp) {
		BigDecimal bDeg = new BigDecimal(deg);
		String lat = toDMS(bDeg, format, dp);

		if ("".equals(lat)) {
			return "-";
		} else {
			return lat + ((bDeg.compareTo(BigDecimal.ZERO) < 0) ? '西' : '东');
		}
	}

	public static String parseDMS(String dms) {
		String[] arrDms = dms.trim().replace("-", "").substring(0, dms.length()-1)
				.split("[^0-9.,+]");
		//.replace("/([^\u0000-\u00FF])/g", "")
		String retStr="";
		BigDecimal deg = new BigDecimal(0);
		switch (arrDms.length) {
		case 3: // interpret 3-part result as d/m/s
			deg = new BigDecimal(arrDms[0]).add(
					new BigDecimal(arrDms[1]).divide(new BigDecimal(60),5,BigDecimal.ROUND_HALF_UP)).add(
					new BigDecimal(arrDms[2]).divide(new BigDecimal(3600),5,BigDecimal.ROUND_HALF_UP));
			break;
		case 2: // interpret 2-part result as d/m
			deg = new BigDecimal(arrDms[0]).add(new BigDecimal(arrDms[1])
					.divide(new BigDecimal(60),5,BigDecimal.ROUND_HALF_UP));
			break;
		case 1: // just d (possibly decimal) or non-separated dddmmss
			deg = new BigDecimal(arrDms[0]);
			// check for fixed-width unseparated format eg 0033709W
			// if (/[NS]/i.test(dmsStr)) deg = '0' + deg; // - normalise N/S to
			// 3-digit degrees
			// if (/[0-9]{7}/.test(deg)) deg = deg.slice(0,3)/1 +
			// deg.slice(3,5)/60 + deg.slice(5)/3600;
			break;
		default:
			
		}
		
		if(dms.endsWith("西") || dms.endsWith("南")){
			retStr = "-"+deg.toString();
		} else {
			retStr = deg.toString();
		}

		return retStr;
	}

	public static void main(String[] args) {
		System.out.println(parseDMS("031°22′17.1南"));
		System.out.println(parseDMS("120°44′01.7″东"));
		System.out.println(toLat("31.37142", "dms", 1));
		System.out.println(toLon("120.73380", "dms", 1));
	}
}
