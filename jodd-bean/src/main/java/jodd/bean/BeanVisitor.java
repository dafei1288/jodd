// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.util.InExRuleMatcher;
import jodd.util.InExRules;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Visitor for bean properties. It extracts properties names
 * from the source bean and then visits one by one.
 */
public abstract class BeanVisitor implements InExRuleMatcher<String, String> {

	/**
	 * Source bean.
	 */
	protected Object source;
	/**
	 * Include/exclude rules.
	 */
	protected InExRules<String, String> rules = new InExRules<String, String>(this);
	/**
	 * Flag for enabling declared properties, or just public ones.
	 */
	protected boolean declared;
	/**
	 * Defines if null values should be ignored.
	 */
	protected boolean ignoreNullValues;
	/**
	 * Defines if fields should be included.
	 */
	protected boolean includeFields;
	/**
	 * Initial matching mode.
	 */
	protected boolean blacklist = true;

	// ---------------------------------------------------------------- util

	/**
	 * Returns all bean property names.
	 */
	protected String[] getAllBeanPropertyNames(Class type, boolean declared) {
		ClassDescriptor classDescriptor = ClassIntrospector.lookup(type);

		PropertyDescriptor[] propertyDescriptors = classDescriptor.getAllPropertyDescriptors();

		ArrayList<String> names = new ArrayList<String>(propertyDescriptors.length);

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			MethodDescriptor getter = propertyDescriptor.getReadMethodDescriptor();
			if (getter != null) {
				if (getter.matchDeclared(declared)) {
					names.add(propertyDescriptor.getName());
				}
			}
			else if (includeFields) {
				FieldDescriptor field = propertyDescriptor.getFieldDescriptor();
				if (field != null) {
					if (field.matchDeclared(declared)) {
						names.add(field.getName());
					}
				}
			}
		}

		return names.toArray(new String[names.size()]);
	}

	/**
	 * Returns an array of bean properties. If bean is a <code>Map</code>,
	 * all its keys will be returned.
	 */
	protected String[] resolveProperties(Object bean, boolean declared) {
		String[] properties;

		if (bean instanceof Map) {
			Set keys = ((Map) bean).keySet();

			properties = new String[keys.size()];
			int ndx = 0;
			for (Object key : keys) {
				properties[ndx] = key.toString();
				ndx++;
			}
		} else {
			properties = getAllBeanPropertyNames(bean.getClass(), declared);
		}

		return properties;
	}

	/**
	 * Starts visiting properties.
	 */
	public void visit() {
		String[] properties = resolveProperties(source, declared);

		for (String name : properties) {
			if (name == null) {
				continue;
			}

			if (!rules.match(name, blacklist)) {
				continue;
			}

			Object value;

			if (declared) {
				value = BeanUtil.getDeclaredProperty(source, name);
			} else {
				value = BeanUtil.getProperty(source, name);
			}

			if (value == null && ignoreNullValues) {
				continue;
			}

			visitProperty(name, value);
		}
	}

	/**
	 * Invoked for each visited property. Returns <code>true</code> if
	 * visiting should continue, otherwise <code>false</code> to stop.
	 */
	protected abstract boolean visitProperty(String name, Object value);

	/**
	 * Compares property name to the rules.
	 */
	public boolean accept(String propertyName, String rule, boolean include) {
		return propertyName.equals(rule);
	}
}