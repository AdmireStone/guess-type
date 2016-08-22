package org.vlis.operations.event.typeguess;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author thinking_fioa , E-mail: thinking_fioa@163.com Create Time :
 *         2016年6月29日 下午4:09:59
 *
 */
public class DistinguishKeyTypeHelper {

	private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(?:true|True|TRUE|false|False|FALSE)$");
	private static final Pattern DOUBLE_PATTERN = Pattern
			.compile("^(?:" + "[-+]?(?:[0-9][0-9_]*)\\.[0-9_]*(?:[eE][-+][0-9]+)?"
					+ "|[-+]?(?:[0-9][0-9_]*)?\\.[0-9_]+(?:[eE][-+][0-9]+)?"
					+ "|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*)$");
	private static final Pattern LONG_PATTERN = Pattern.compile("^(?:" + "[-+]?0b[0-1_]+|[-+]?0[0-7_]+"
			+ "|[-+]?(?:0|[1-9][0-9_]*)" + "|[-+]?0x[0-9a-fA-F_]+" + "|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");
	private static final Pattern DATE_PATTERN = Pattern
			.compile("^(?:" + "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
					+ "[-/](((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)"
					+ "[-/](0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))) "
					+ "((((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");

	public static DistinguishKeyTypeHelper getInstance() {
		return DistinguishKeyTypeHelperHolder.distinguishKeyTypeHelper;
	}

	private static class DistinguishKeyTypeHelperHolder {
		private static final DistinguishKeyTypeHelper distinguishKeyTypeHelper = new DistinguishKeyTypeHelper();
	}

	public boolean isBooleanType(String str) {
		return BOOLEAN_PATTERN.matcher(str).matches();
	}

	public boolean isDoubleType(String str) {
		return DOUBLE_PATTERN.matcher(str).matches();
	}

	public boolean isDateType(String str) {
		//防止出现形如"2014-03-31 14:20:59.0"的日期格式 
		if (str.contains(".")) {
			int p = str.lastIndexOf(".");
			str = str.substring(0, p);
		}
		return DATE_PATTERN.matcher(str).matches();
	}

	public boolean isLongType(String str) {
		return LONG_PATTERN.matcher(str).matches();
	}

	@Test
	public void test() {
		System.out.println(isDateType("2014-03-31 14:20:59.0"));
	}
}
