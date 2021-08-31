package com.nonelonely.component.email.enums;

/**
 * 描述:Redis事件枚举
 *
 * @author 杨建全
 * @date 2017年11月15日 上午9:25:05
 */
public enum RedisEventEnums {
	// 重启邮件抓取任务事件
	RebootGetMailTask("RebootGetMailTask");

	private RedisEventEnums(String v) {
		this.v = v;
	}

	private String v;

	public String getValue() {
		return v;
	}

	public void setValue(String v) {
		this.v = v;
	}

	public static RedisEventEnums getByValue(String value) {
		for (RedisEventEnums re : values()) {
			if (re.getValue().equals(value)) {
				return re;
			}
		}
		return null;
	}

}
