package org.hisp.dhis.organisationunit;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
public class OrganisationUnitDataSetAssociationSet
{
    /**
     * List of data set association sets.
     */
    private List<Set<Integer>> dataSetAssociationSets = new ArrayList<Set<Integer>>();
    
    /**
     * Mapping between organisation unit identifier and index of association set in list.
     */
    private Map<Integer, Integer> organisationUnitAssociationSetMap = new HashMap<Integer, Integer>();

    /**
     * Set of distinct data sets in all association sets.
     */
    private Set<Integer> distinctDataSets = new HashSet<Integer>();
    
    public OrganisationUnitDataSetAssociationSet()
    {
    }

    public List<Set<Integer>> getDataSetAssociationSets()
    {
        return dataSetAssociationSets;
    }

    public void setDataSetAssociationSets( List<Set<Integer>> dataSetAssociationSets )
    {
        this.dataSetAssociationSets = dataSetAssociationSets;
    }

    public Map<Integer, Integer> getOrganisationUnitAssociationSetMap()
    {
        return organisationUnitAssociationSetMap;
    }

    public void setOrganisationUnitAssociationSetMap( Map<Integer, Integer> organisationUnitAssociationSetMap )
    {
        this.organisationUnitAssociationSetMap = organisationUnitAssociationSetMap;
    }

    public Set<Integer> getDistinctDataSets()
    {
        return distinctDataSets;
    }

    public void setDistinctDataSets( Set<Integer> distinctDataSets )
    {
        this.distinctDataSets = distinctDataSets;
    }
}
