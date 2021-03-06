/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.staging.LayoutStagingUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.LayoutSet;
import com.liferay.portal.model.LayoutSetBranch;
import com.liferay.portal.model.LayoutSetStagingHandler;
import com.liferay.portal.model.impl.ColorSchemeImpl;
import com.liferay.portal.model.impl.ThemeImpl;

import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 */
public class LayoutSetLocalServiceStagingAdvice
	extends LayoutSetLocalServiceImpl implements MethodInterceptor {

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Method method = methodInvocation.getMethod();

		String methodName = method.getName();

		Object[] arguments = methodInvocation.getArguments();

		if (!_layoutSetLocalServiceStagingAdviceMethodNames.contains(
				methodName)) {

			return wrapReturnValue(methodInvocation.proceed());
		}

		Object returnValue = null;

		if (methodName.equals("updateLayoutSetPrototypeLinkEnabled") &&
			(arguments.length == 5)) {

			updateLayoutSetPrototypeLinkEnabled(
				(Long)arguments[0], (Boolean)arguments[1],
				(Boolean)arguments[2], (String)arguments[3]);
		}
		else if (methodName.equals("updateLogo") && (arguments.length == 5)) {
			updateLogo(
				(Long)arguments[0], (Boolean)arguments[1],
				(Boolean)arguments[2], (InputStream)arguments[3],
				(Boolean)arguments[4]);
		}
		else if (methodName.equals("updateLookAndFeel") &&
				(arguments.length == 6)) {

			returnValue = updateLookAndFeel(
				(Long)arguments[0], (Boolean)arguments[1],
				(String)arguments[2], (String)arguments[3],
				(String)arguments[4], (Boolean)arguments[5]);
		}
		else if (methodName.equals("updateSettings")) {
			returnValue = updateSettings(
				(Long)arguments[0], (Boolean)arguments[1],
				(String)arguments[2]);
		}
		else {
			try {
				Class<?> clazz = getClass();

				Method localMethod = clazz.getMethod(
					methodName, method.getParameterTypes());

				returnValue = localMethod.invoke(this, arguments);
			}
			catch (InvocationTargetException ite) {
				throw ite.getTargetException();
			}
			catch (NoSuchMethodException nsme) {
				throw new SystemException(nsme);
			}
		}

		return wrapReturnValue(returnValue);
	}

	@Override
	public void updateLayoutSetPrototypeLinkEnabled(
			long groupId, boolean privateLayout,
			boolean layoutSetPrototypeLinkEnabled,
			String layoutSetPrototypeUuid)
		throws PortalException, SystemException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		layoutSet = wrapLayoutSet(layoutSet);

		LayoutSetBranch layoutSetBranch = LayoutStagingUtil.getLayoutSetBranch(
			layoutSet);

		if (layoutSetBranch == null) {
			super.updateLayoutSetPrototypeLinkEnabled(
				groupId, privateLayout, layoutSetPrototypeLinkEnabled,
				layoutSetPrototypeUuid);

			return;
		}

		if (Validator.isNull(layoutSetPrototypeUuid)) {
			layoutSetPrototypeUuid =
				layoutSetBranch.getLayoutSetPrototypeUuid();
		}

		if (Validator.isNull(layoutSetPrototypeUuid) &&
			layoutSetPrototypeLinkEnabled) {

			throw new IllegalStateException(
				"Cannot set layoutSetPrototypeLinkEnabled to true when " +
					"layoutSetPrototypeUuid is null");
		}

		layoutSetBranch.setLayoutSetPrototypeLinkEnabled(
			layoutSetPrototypeLinkEnabled);
		layoutSetBranch.setLayoutSetPrototypeUuid(layoutSetPrototypeUuid);

		layoutSetBranchPersistence.update(layoutSetBranch, false);
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean logo, InputStream is,
			boolean cleanUpStream)
		throws PortalException, SystemException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		layoutSet = wrapLayoutSet(layoutSet);

		LayoutSetBranch layoutSetBranch = LayoutStagingUtil.getLayoutSetBranch(
			layoutSet);

		if (layoutSetBranch == null) {
			return super.updateLogo(
				groupId, privateLayout, logo, is, cleanUpStream);
		}

		layoutSetBranch.setModifiedDate(new Date());
		layoutSetBranch.setLogo(logo);

		if (logo) {
			long logoId = layoutSetBranch.getLogoId();

			if (logoId <= 0) {
				logoId = counterLocalService.increment();

				layoutSet.setLogoId(logoId);
			}
		}
		else {
			layoutSet.setLogoId(0);
		}

		layoutSetBranchPersistence.update(layoutSetBranch, false);

		if (logo) {
			imageLocalService.updateImage(
				layoutSetBranch.getLogoId(), is, cleanUpStream);
		}
		else {
			imageLocalService.deleteImage(layoutSetBranch.getLogoId());
		}

		return layoutSet;
	}

	@Override
	public LayoutSet updateLookAndFeel(
			long groupId, boolean privateLayout, String themeId,
			String colorSchemeId, String css, boolean wapTheme)
		throws PortalException, SystemException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		layoutSet = wrapLayoutSet(layoutSet);

		LayoutSetBranch layoutSetBranch = LayoutStagingUtil.getLayoutSetBranch(
			layoutSet);

		if (layoutSetBranch == null) {
			return super.updateLookAndFeel(
				groupId, privateLayout, themeId, colorSchemeId, css, wapTheme);
		}

		layoutSetBranch.setModifiedDate(new Date());

		if (Validator.isNull(themeId)) {
			themeId = ThemeImpl.getDefaultRegularThemeId(
				layoutSetBranch.getCompanyId());
		}

		if (Validator.isNull(colorSchemeId)) {
			colorSchemeId = ColorSchemeImpl.getDefaultRegularColorSchemeId();
		}

		if (wapTheme) {
			layoutSetBranch.setWapThemeId(themeId);
			layoutSetBranch.setWapColorSchemeId(colorSchemeId);
		}
		else {
			layoutSetBranch.setThemeId(themeId);
			layoutSetBranch.setColorSchemeId(colorSchemeId);
			layoutSetBranch.setCss(css);
		}

		layoutSetBranchPersistence.update(layoutSetBranch, false);

		return layoutSet;
	}

	@Override
	public LayoutSet updateSettings(
			long groupId, boolean privateLayout, String settings)
		throws PortalException, SystemException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		layoutSet = wrapLayoutSet(layoutSet);

		LayoutSetBranch layoutSetBranch = LayoutStagingUtil.getLayoutSetBranch(
			layoutSet);

		if (layoutSetBranch == null) {
			return super.updateSettings(groupId, privateLayout, settings);
		}

		layoutSetBranch.setModifiedDate(new Date());
		layoutSetBranch.setSettings(settings);

		layoutSetBranchPersistence.update(layoutSetBranch, false);

		return layoutSet;
	}

	protected LayoutSet unwrapLayoutSet(LayoutSet layoutSet) {
		LayoutSetStagingHandler layoutSetStagingHandler =
			LayoutStagingUtil.getLayoutSetStagingHandler(layoutSet);

		if (layoutSetStagingHandler == null) {
			return layoutSet;
		}

		return layoutSetStagingHandler.getLayoutSet();
	}

	protected LayoutSet wrapLayoutSet(LayoutSet layoutSet) {
		LayoutSetStagingHandler layoutSetStagingHandler =
			LayoutStagingUtil.getLayoutSetStagingHandler(layoutSet);

		if (layoutSetStagingHandler != null) {
			return layoutSet;
		}

		Group group = null;

		try {
			group = layoutSet.getGroup();
		}
		catch (Exception e) {
			return layoutSet;
		}

		if (!LayoutStagingUtil.isBranchingLayoutSet(
			group, layoutSet.getPrivateLayout())) {

			return layoutSet;
		}

		return (LayoutSet)ProxyUtil.newProxyInstance(
			PortalClassLoaderUtil.getClassLoader(),
			new Class[] {LayoutSet.class},
			new LayoutSetStagingHandler(layoutSet));
	}

	protected List<LayoutSet> wrapLayoutSets(List<LayoutSet> layoutSets) {
		if (layoutSets.isEmpty()) {
			return layoutSets;
		}

		List<LayoutSet> wrappedLayoutSets = new ArrayList<LayoutSet>(
			layoutSets.size());

		for (int i = 0; i < layoutSets.size(); i++) {
			LayoutSet wrappedLayoutSet = wrapLayoutSet(layoutSets.get(i));

			wrappedLayoutSets.add(wrappedLayoutSet);
		}

		return wrappedLayoutSets;
	}

	protected Object wrapReturnValue(Object returnValue) {
		if (returnValue instanceof LayoutSet) {
			returnValue = wrapLayoutSet((LayoutSet)returnValue);
		}
		else if (returnValue instanceof List<?>) {
			returnValue = wrapLayoutSets((List<LayoutSet>)returnValue);
		}

		return returnValue;
	}

	private static Set<String> _layoutSetLocalServiceStagingAdviceMethodNames =
		new HashSet<String>();

	static {
		_layoutSetLocalServiceStagingAdviceMethodNames.add(
			"updateLayoutSetPrototypeLinkEnabled");
		_layoutSetLocalServiceStagingAdviceMethodNames.add("updateLogo");
		_layoutSetLocalServiceStagingAdviceMethodNames.add("updateLookAndFeel");
		_layoutSetLocalServiceStagingAdviceMethodNames.add("updateSettings");
	}

}