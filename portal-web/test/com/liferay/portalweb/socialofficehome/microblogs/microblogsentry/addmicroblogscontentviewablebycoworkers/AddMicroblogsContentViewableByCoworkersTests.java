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

package com.liferay.portalweb.socialofficehome.microblogs.microblogsentry.addmicroblogscontentviewablebycoworkers;

import com.liferay.portalweb.portal.BaseTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class AddMicroblogsContentViewableByCoworkersTests extends BaseTestSuite {
	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(AddUserSOCoworkerTest.class);
		testSuite.addTestSuite(AddMicroblogsContentViewableByCoworkersTest.class);
		testSuite.addTestSuite(ViewMicroblogsEntryContentViewableByCoworkersTest.class);
		testSuite.addTestSuite(SignOutTest.class);
		testSuite.addTestSuite(SOCo_NewPasswordTest.class);
		testSuite.addTestSuite(SignOutTest.class);
		testSuite.addTestSuite(SOCo_SignInTest.class);
		testSuite.addTestSuite(SOCo_ViewMicroblogsContentCoworkersTest.class);
		testSuite.addTestSuite(SOCo_AddAsCoworkerCCActionsTest.class);
		testSuite.addTestSuite(SignOutTest.class);
		testSuite.addTestSuite(SOSignInTest.class);
		testSuite.addTestSuite(ConfirmCCCoworkerRequestTest.class);
		testSuite.addTestSuite(SignOutTest.class);
		testSuite.addTestSuite(SOCo_SignInTest.class);
		testSuite.addTestSuite(SOCo_ViewCoworkerMicroblogsContentCoworkersTest.class);
		testSuite.addTestSuite(SignOutTest.class);
		testSuite.addTestSuite(SOSignInTest.class);
		testSuite.addTestSuite(TearDownWHEntryContentTest.class);
		testSuite.addTestSuite(TearDownSOUserTest.class);

		return testSuite;
	}
}