/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.behaviortrees;

import com.badlogic.gdx.ai.utils.random.ConstantDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.ConstantLongDistribution;
import com.badlogic.gdx.ai.utils.random.Distribution;
import com.badlogic.gdx.ai.utils.random.GaussianDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.GaussianFloatDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularFloatDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularLongDistribution;
import com.badlogic.gdx.ai.utils.random.UniformDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.UniformFloatDistribution;
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.UniformLongDistribution;

/**
 * The Purpose of this class is to convert the many variants of Distributions
 * to and from strings
 * @author MeFisto94
 */
public class DistributionStringConverter {
    
    /* 
    if (distribution instanceof UniformIntegerDistribution) {
            UniformIntegerDistribution uid = (UniformIntegerDistribution)distribution;
            return "uniform," + uid.getLow() + "," + uid.getHigh();
    }
    if (distribution instanceof UniformLongDistribution) {
            UniformLongDistribution uld = (UniformLongDistribution)distribution;
            return "uniform," + uld.getLow() + "," + uld.getHigh();
    }
    if (distribution instanceof UniformFloatDistribution) {
            UniformFloatDistribution ufd = (UniformFloatDistribution)distribution;
            return "uniform," + ufd.getLow() + "," + ufd.getHigh();
    }
    if (distribution instanceof UniformDoubleDistribution) {
            UniformDoubleDistribution udd = (UniformDoubleDistribution)distribution;
            return "uniform," + udd.getLow() + "," + udd.getHigh();
    }
    */
    
    public static String fromDistribution(ConstantIntegerDistribution cid) {
        // Constant could be a short cut (leave out "constant", but we don't do that
        return "constant," + cid.getValue();
    }
    
    public static String fromDistribution(ConstantLongDistribution cld) {
        return "constant," + cld.getValue();
    }
    
    public static String fromDistribution(ConstantFloatDistribution cfd) {
        return "constant," + cfd.getValue();
    }
    
    public static String fromDistribution(ConstantDoubleDistribution cdd) {
        return "constant," + cdd.getValue();
    }

    public static String fromDistribution(GaussianFloatDistribution gfd) {
        return "gaussian," + gfd.getMean() + "," + gfd.getStandardDeviation();
    }
    
    public static String fromDistribution(GaussianDoubleDistribution gdd) {
        return "gaussian," + gdd.getMean() + "," + gdd.getStandardDeviation();
    }
    
    public static String fromDistribution(TriangularIntegerDistribution tid) {
        return "triangular," + tid.getLow() + "," + tid.getHigh() + "," + tid.getMode();
    }
    
    public static String fromDistribution(TriangularLongDistribution tld) {
        return "triangular," + tld.getLow() + "," + tld.getHigh() + "," + tld.getMode();
    }

    public static String fromDistribution(TriangularFloatDistribution tfd) {
        return "triangular," + tfd.getLow() + "," + tfd.getHigh() + "," + tfd.getMode();
    }
    
    public static String fromDistribution(TriangularDoubleDistribution tdd) {
        return "triangular," + tdd.getLow() + "," + tdd.getHigh() + "," + tdd.getMode();
    }
    
    public static String fromDistribution(UniformIntegerDistribution uid) {
        return "uniform," + uid.getLow() + "," + uid.getHigh();
    }
    
    public static String fromDistribution(UniformLongDistribution uld) {
        return "uniform," + uld.getLow() + "," + uld.getHigh();
    }
    
    public static String fromDistribution(UniformFloatDistribution ufd) {
        return "uniform," + ufd.getLow() + "," + ufd.getHigh();
    }
    
    public static String fromDistribution(UniformDoubleDistribution udd) {
        return "uniform," + udd.getLow() + "," + udd.getHigh();
    }

    public static String fromDistribution(Distribution d) {
        if (d instanceof ConstantIntegerDistribution) {
            return fromDistribution((ConstantIntegerDistribution)d);
        }
        
        if (d instanceof ConstantLongDistribution) {
            return fromDistribution((ConstantLongDistribution)d);
        }
        
        if (d instanceof ConstantFloatDistribution) {
            return fromDistribution((ConstantFloatDistribution)d);
        }
        
        if (d instanceof ConstantDoubleDistribution) {
            return fromDistribution((ConstantDoubleDistribution)d);
        }
        
        if (d instanceof GaussianFloatDistribution) {
            return fromDistribution((GaussianFloatDistribution)d);
        }
        
        if (d instanceof GaussianDoubleDistribution) {
            return fromDistribution((GaussianDoubleDistribution)d);
        }
        
        if (d instanceof TriangularIntegerDistribution) {
            return fromDistribution((TriangularIntegerDistribution)d);
        }
        
        if (d instanceof TriangularLongDistribution) {
            return fromDistribution((TriangularLongDistribution)d);
        }
        
        if (d instanceof TriangularFloatDistribution) {
            return fromDistribution((TriangularFloatDistribution)d);
        }
        
        if (d instanceof TriangularDoubleDistribution) {
            return fromDistribution((TriangularDoubleDistribution)d);
        }
        
        if (d instanceof UniformIntegerDistribution) {
            return fromDistribution((UniformIntegerDistribution)d);
        }
        
        if (d instanceof UniformLongDistribution) {
            return fromDistribution((UniformLongDistribution)d);
        }
        
        if (d instanceof UniformFloatDistribution) {
            return fromDistribution((UniformFloatDistribution)d);
        }
        
        if (d instanceof UniformDoubleDistribution) {
            return fromDistribution((UniformDoubleDistribution)d);
        }
        
        return null;
    }
}
