/*
 * (C) Copyright 2002-2013 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.ecm.platform.importer.xml.parser.test;

import com.google.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.importer.xml.parser.XMLImporterService;
import org.nuxeo.ecm.platform.importer.xml.parser.XMLImporterServiceImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Verify Service mapping with multi-value metadata
 *
 * @author <a href="mailto:mvachette@nuxeo.com">Mika</a>
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy("nuxeo-importer-xml-parser")
@LocalDeploy("nuxeo-importer-xml-parser:test-ImporterMapping-MultiValue-contrib.xml")
public class TestMapperServiceMultiValue {

    @Inject
    CoreSession session;

    @Test
    public void test() throws Exception {

        File xml = FileUtils.getResourceFileFromContext("multivalue.xml");
        Assert.assertNotNull(xml);

        DocumentModel root = session.getRootDocument();

        XMLImporterService importer = Framework.getLocalService(XMLImporterService.class);
        Assert.assertNotNull(importer);
        importer.importDocuments(root, xml);

        session.save();

        List<DocumentModel> docs = session.query("select * from Document where dc:title='MultiValue'");
        Assert.assertEquals("we should have only one File", 1, docs.size());
        DocumentModel fileDoc = docs.get(0);
        Property property = fileDoc.getProperty("dc:subjects");
        Object[] subjects = (Object[]) property.getValue();
        Assert.assertEquals("The property dc:subjects should contain 2 values", 2, subjects.length);
    }

}
