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

package com.liferay.portal.security.permission;

/**
 * @author Brian Wing Shun Chan
 */
public class SimplePermissionChecker extends BasePermissionChecker {

	@Override
	public SimplePermissionChecker clone() {
		return new SimplePermissionChecker();
	}

	public boolean hasOwnerPermission(
		long companyId, String name, String primKey, long ownerId,
		String actionId) {

		return hasPermission(actionId);
	}

	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		return hasPermission(actionId);
	}

	public boolean hasUserPermission(
		long groupId, String name, String primKey, String actionId,
		boolean checkAdmin) {

		return hasPermission(actionId);
	}

	public boolean isCompanyAdmin() {
		return signedIn;
	}

	public boolean isCompanyAdmin(long companyId) {
		return signedIn;
	}

	public boolean isGroupAdmin(long groupId) {
		return signedIn;
	}

	public boolean isGroupOwner(long groupId) {
		return signedIn;
	}

	protected boolean hasPermission(String actionId) {
		if (signedIn) {
			return true;
		}

		if (actionId.equals(ActionKeys.VIEW)) {
			return true;
		}
		else {
			return false;
		}
	}

}