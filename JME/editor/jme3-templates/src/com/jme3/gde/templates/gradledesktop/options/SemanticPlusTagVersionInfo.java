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

import java.text.Collator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Versioning scheme like x.x.x with or without a tag/type. Tries to parse
 * versions as broadly and leniently as possible
 */
public final class SemanticPlusTagVersionInfo implements VersionInfo {

    private static final Logger logger = Logger.getLogger(SemanticPlusTagVersionInfo.class.getName());

    private static final Pattern VERSION_PATTERN = Pattern.compile("(?<major>\\d+).?(?<minor>\\d*).?(?<release>\\d*)\\W?(?<tag>.*)");

    private final Integer major;
    private final Integer minor;
    private final Integer release;
    private final String tag;
    private final String versionString;

    public SemanticPlusTagVersionInfo(String versionString) {
        this.versionString = versionString;

        Matcher m = VERSION_PATTERN.matcher(versionString);
        if (m.find()) {
            String group = m.group("major");
            this.major = group.isEmpty() ? null : Integer.valueOf(group);
            group = m.group("minor");
            this.minor = group.isEmpty() ? null : Integer.valueOf(group);
            group = m.group("release");
            this.release = group.isEmpty() ? null : Integer.valueOf(group);
            group = m.group("tag");
            this.tag = group.isEmpty() ? null : group;
        } else {
            this.major = null;
            this.minor = null;
            this.release = null;
            this.tag = null;

            logger.log(Level.INFO, "Failed to parse version information from version string {0}", versionString);
        }
    }

    public static VersionInfo of(String versionString) {
        return new SemanticPlusTagVersionInfo(versionString);
    }

    @Override
    public Integer getMajor() {
        return major;
    }

    @Override
    public Integer getMinor() {
        return minor;
    }

    @Override
    public Integer getRelease() {
        return release;
    }

    @Override
    public String getType() {
        return tag;
    }

    @Override
    public String getVersionString() {
        return versionString;
    }

    @Override
    public int compareTo(VersionInfo o) {
        int result = compareVersionDigit(getMajor(), o.getMajor());
        if (result != 0) {
            return result;
        }
        result = compareVersionDigit(getMinor(), o.getMinor());
        if (result != 0) {
            return result;
        }
        result = compareVersionDigit(getRelease(), o.getRelease());
        if (result != 0) {
            return result;
        }

        result = Collator.getInstance().compare(getType() != null ? getType() : "", o.getType() != null ? o.getType() : "");
        if (result != 0) {
            return result;
        }

        return Collator.getInstance().compare(getVersionString(), o.getVersionString());
    }

    private int compareVersionDigit(Integer versionDigit1, Integer versionDigit2) {
        if (versionDigit1 == null || versionDigit2 == null) {
            return 0;
        }

        return Integer.compare(versionDigit1, versionDigit2);
    }

    @Override
    public String toString() {
        return versionString;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.versionString);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof VersionInfo)) {
            return false;
        }
        final VersionInfo other = (VersionInfo) obj;
        return Objects.equals(this.versionString, other.getVersionString());
    }

}
