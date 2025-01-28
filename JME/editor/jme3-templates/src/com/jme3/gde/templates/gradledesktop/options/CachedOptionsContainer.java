/*
 * Copyright (c) 2009-2023 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.templates.gradledesktop.options;

import com.jme3.gde.templates.utils.mavensearch.MavenApiVersionChecker;
import com.jme3.gde.templates.utils.mavensearch.MavenVersionChecker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Singleton that contains all the options. Tries to go online to get all the
 * latest options
 */
public class CachedOptionsContainer {

    private static CachedOptionsContainer instance;

    private static final Logger logger = Logger.getLogger(CachedOptionsContainer.class.getName());

    private static final Map<TemplateLibrary, Predicate<VersionInfo>> LIBRARY_VERSION_FILTERS = Map.ofEntries(
            entry(PhysicsLibrary.MINIE, (version) -> {
                return version.getType() == null;
            }),
            entry(AdditionalLibrary.HEART, (version) -> {
                return version.getType() == null;
            })
    );

    private List<LibraryVersion> jmeVersions;
    private List<TemplateLibrary> additionalLibraries;
    private List<TemplateLibrary> guiLibraries;
    private List<TemplateLibrary> networkingLibraries;
    private List<TemplateLibrary> physicsLibraries;

    private CachedOptionsContainer() {
        initialize();
    }

    public static CachedOptionsContainer getInstance() {
        if (instance == null) {
            synchronized (CachedOptionsContainer.class) {
                if (instance == null) {
                    instance = new CachedOptionsContainer();
                }
            }
        }
        return instance;
    }

    private void initialize() {
        MavenVersionChecker mavenVersionChecker = new MavenApiVersionChecker();

        jmeVersions = initVersions(mavenVersionChecker,
                MavenArtifact.JME_GROUP_ID,
                JMEVersion.JME_ARTIFACT_ID,
                (jmeVersion) -> {
                    return "stable".equalsIgnoreCase(jmeVersion.getType());
                },
                JMEVersion.values(), (result) -> {
            jmeVersions = result;
        },
                JMEVersion.DEFAULT_PATCH_NOTES_PATH);
        additionalLibraries = initLibaries(mavenVersionChecker, AdditionalLibrary.values());
        guiLibraries = initLibaries(mavenVersionChecker, GUILibrary.values());
        networkingLibraries = initLibaries(mavenVersionChecker, NetworkingLibrary.values());
        physicsLibraries = initLibaries(mavenVersionChecker, PhysicsLibrary.values());
    }

    private static List<TemplateLibrary> initLibaries(final MavenVersionChecker mavenVersionChecker, TemplateLibrary[] libraries) {
        List<TemplateLibrary> libs = new ArrayList<>(libraries.length);
        for (TemplateLibrary templateLibrary : libraries) {
            libs.add(createTemplateLibrary(templateLibrary, mavenVersionChecker));
        }

        return Collections.unmodifiableList(libs);
    }

    private static TemplateLibrary createTemplateLibrary(TemplateLibrary templateLibrary, final MavenVersionChecker mavenVersionChecker) {
        return new TemplateLibrary() {

            private VersionInfo version;

            {
                if (templateLibrary.getGroupId() != null && templateLibrary.getArtifactId() != null) {
                    if (LIBRARY_VERSION_FILTERS.containsKey(templateLibrary)) {
                        mavenVersionChecker.getAllVersions(templateLibrary.getGroupId(), templateLibrary.getArtifactId())
                                .whenComplete((result, exception) -> {
                                    if (exception != null || result == null) {
                                        logMavenCheckFailure(exception);

                                        return;
                                    }

                                    Predicate<VersionInfo> versionFilter = LIBRARY_VERSION_FILTERS.get(templateLibrary);
                                    Optional<VersionInfo> latestInfo = result.stream()
                                            .map((versionString) -> {
                                                return SemanticPlusTagVersionInfo.of(versionString);
                                            })
                                            .filter(versionFilter)
                                            .max(Comparator.naturalOrder());
                                    if (latestInfo.isPresent()) {
                                        version = latestInfo.get();
                                    }
                                });
                    } else {
                        mavenVersionChecker.getLatestVersion(templateLibrary.getGroupId(), templateLibrary.getArtifactId())
                                .whenComplete((result, exception) -> {
                                    if (exception != null || result == null) {
                                        logMavenCheckFailure(exception);

                                        return;
                                    }

                                    version = SemanticPlusTagVersionInfo.of(result);
                                });
                    }
                }
            }

            private void logMavenCheckFailure(Throwable exception) {
                logger.log(Level.INFO, exception,
                        () -> String.format("Failed to acquire version information for Maven artifact %s (%s:%s)", new Object[]{getLabel(), getGroupId(), getArtifactId()}));
            }

            @Override
            public String getLabel() {
                return templateLibrary.getLabel();
            }

            @Override
            public String getDescription() {
                return templateLibrary.getDescription();
            }

            @Override
            public boolean getIsCoreJmeLibrary() {
                return templateLibrary.getIsCoreJmeLibrary();
            }

            @Override
            public String getGroupId() {
                return templateLibrary.getGroupId();
            }

            @Override
            public String getArtifactId() {
                return templateLibrary.getArtifactId();
            }

            @Override
            public String toString() {
                return templateLibrary.getLabel();
            }

            @Override
            public VersionInfo getVersionInfo() {
                return version != null ? version : templateLibrary.getVersionInfo();
            }

        };
    }

    public List<TemplateLibrary> getAdditionalLibraries() {
        return additionalLibraries;
    }

    public List<TemplateLibrary> getGuiLibraries() {
        return guiLibraries;
    }

    public List<TemplateLibrary> getNetworkingLibraries() {
        return networkingLibraries;
    }

    public List<TemplateLibrary> getPhysicsLibraries() {
        return physicsLibraries;
    }

    public List<LibraryVersion> getJmeVersions() {
        return jmeVersions;
    }

    /**
     * Initialize a version listing from Maven (and the given hard coded list)
     *
     * @param <T> the type of version information should be used in comparison
     * @param mavenVersionChecker access to Maven version information
     * @param groupId Maven group ID
     * @param artifactId Maven artifact ID
     * @param versionFilter predicate for version inclusion, may be null (all
     * versions are accepted)
     * @param versions the hard coded list of versions, guaranteed to be
     * included in the listing
     * @param completedVersionsConsumer consumer for the versions listing that
     * has been compiled from hard coded list and Maven version results. Only
     * triggers if Maven version check is successful
     * @param defaultPatchNotes for versions from Maven API, we don't get their
     * release notes. Supply default patch notes
     * @return returns a listing of hard coded versions immediately
     */
    private static List<LibraryVersion> initVersions(MavenVersionChecker mavenVersionChecker, String groupId,
            String artifactId, Predicate<VersionInfo> versionFilter,
            LibraryVersion[] versions, Consumer<List<LibraryVersion>> completedVersionsConsumer,
            String defaultPatchNotes) {
        mavenVersionChecker.getAllVersions(groupId, artifactId).whenComplete((result, exception) -> {

            if (exception != null || result == null) {
                logger.log(Level.INFO, exception,
                        () -> String.format("Failed to acquire version information for Maven artifact %s:%s", new Object[]{groupId, artifactId}));

                return;
            }

            initVersionList(result, versionFilter, versions, groupId, artifactId, completedVersionsConsumer, defaultPatchNotes);
        });

        return Collections.unmodifiableList(Arrays.asList(versions));
    }

    private static void initVersionList(List<String> result, Predicate<VersionInfo> versionFilter,
            LibraryVersion[] versions, String groupId, String artifactId,
            Consumer<List<LibraryVersion>> completedVersionsConsumer,
            String defaultPatchNotes) {

        // Filter the versions list
        Stream<VersionInfo> versionStream = result.stream().map((versionString) -> SemanticPlusTagVersionInfo.of(versionString));
        if (versionFilter != null) {
            versionStream = versionStream.filter(versionFilter);
        }
        List<VersionInfo> versionInfoList = versionStream.collect(Collectors.toList());

        // Compile the results
        final SortedSet<LibraryVersion> allVersions = new TreeSet<>(Comparator.comparing(LibraryVersion::getVersionInfo, Comparator.reverseOrder()));
        allVersions.addAll(Arrays.asList(versions));
        for (VersionInfo versionInfo : versionInfoList) {
            allVersions.add(createLibraryVersion(groupId, artifactId, defaultPatchNotes, versionInfo));
        }

        completedVersionsConsumer.accept(Collections.unmodifiableList(new ArrayList<>(allVersions)));
    }

    private static LibraryVersion createLibraryVersion(String groupId, String artifactId, String defaultPatchNotes, VersionInfo versionInfo) {
        return new LibraryVersion() {

            @Override
            public String getGroupId() {
                return groupId;
            }

            @Override
            public String getArtifactId() {
                return artifactId;
            }

            @Override
            public String getPatchNotesPath() {
                return defaultPatchNotes;
            }

            @Override
            public String toString() {
                return getVersionInfo().getVersionString();
            }

            @Override
            public VersionInfo getVersionInfo() {
                return versionInfo;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(versionInfo.getVersionString());
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (!(obj instanceof LibraryVersion)) {
                    return false;
                }
                final LibraryVersion other = (LibraryVersion) obj;

                return Objects.equals(getVersionInfo().getVersionString(), other.getVersionInfo().getVersionString());
            }
        };
    }
}
