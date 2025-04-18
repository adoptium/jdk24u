/*
 * Copyright (c) 2024, Red Hat, Inc.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import tests.Helper;


/*
 * @test
 * @summary Test basic linking from the run-time image with java.base.jmod missing
 *          but java.xml.jmod present. It should link from the run-time image without errors.
 * @requires (jlink.packagedModules & vm.compMode != "Xcomp" & os.maxMemory >= 2g)
 * @library ../../lib /test/lib
 * @enablePreview
 * @modules java.base/jdk.internal.jimage
 *          jdk.jlink/jdk.tools.jlink.internal
 *          jdk.jlink/jdk.tools.jlink.plugin
 *          jdk.jlink/jdk.tools.jimage
 * @build tests.* jdk.test.lib.process.OutputAnalyzer
 *        jdk.test.lib.process.ProcessTools
 * @run main/othervm -Xmx1g BasicJlinkMissingJavaBase
 */
public class BasicJlinkMissingJavaBase extends AbstractLinkableRuntimeTest {

    @Override
    public void runTest(Helper helper, boolean isLinkableRuntime) throws Exception {
        Path finalImage = createJavaXMLRuntimeLink(helper, "java-xml", isLinkableRuntime);
        verifyListModules(finalImage, List.of("java.base", "java.xml"));
    }

    private Path createJavaXMLRuntimeLink(Helper helper, String name, boolean isLinkableRuntime) throws Exception {
        BaseJlinkSpecBuilder builder = new BaseJlinkSpecBuilder();
        builder.helper(helper)
               .name(name)
               .addModule("java.xml")
               .validatingModule("java.xml");
        if (isLinkableRuntime) {
            builder.setLinkableRuntime();
        }
        Set<String> excludedJmods = Set.of("java.base.jmod");
        return createJavaImageRuntimeLink(builder.build(), excludedJmods);
    }

    public static void main(String[] args) throws Exception {
        BasicJlinkMissingJavaBase test = new BasicJlinkMissingJavaBase();
        test.run();
    }

}
