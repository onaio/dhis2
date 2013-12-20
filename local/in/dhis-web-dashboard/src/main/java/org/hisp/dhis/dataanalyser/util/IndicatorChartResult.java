/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
package org.hisp.dhis.dataanalyser.util;


/**
 * @author Mithilesh Kumar Thakur
 *
 * @version IndicatorChartResult.java Nov 3, 2010 12:53:28 PM
 */
public class IndicatorChartResult
{

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
   
    public IndicatorChartResult()
    {
       
    }
   
    public IndicatorChartResult( String[] series, String[] categories, Double data[][],Double numDataArray[][],Double denumDataArray[][],String chartTitle,String xAxis_Title,String yAxis_Title )
    {
        this.series = series;
        this.categories = categories;
        this.data = data;
        this.numDataArray = numDataArray;
        this.denumDataArray = denumDataArray;
      //  this.numData = numData;
      //  this.denumData = denumData;
        this.chartTitle = chartTitle;
        this.xAxis_Title = xAxis_Title;
        this.yAxis_Title = yAxis_Title;
        //this.xseriesList = xseriesList;
        //this.yseriesList = yseriesList;
    }
    
  
    //---------------------------------------------------------------
    // Getters and Setters
    //---------------------------------------------------------------

   
   private String[] series;

    public String[] getSeries()
    {
        return series;
    }

    public void setSeries( String[] series )
    {
        this.series = series;
    }
   
    private String[] categories;

    public String[] getCategories()
    {
        return categories;
    }

    public void setCategories( String[] categories )
    {
        this.categories = categories;
    }
   
    Double data[][];

    public Double[][] getData()
    {
        return data;
    }

    public void setData( Double[][] data )
    {
        this.data = data;
    }
   
    
    Double numDataArray[][];
   
    public Double[][] getNumDataArray()
    {
        return numDataArray;
    }

    public void setNumDataArray( Double[][] numDataArray )
    {
        this.numDataArray = numDataArray;
    }

    Double denumDataArray[][];
    
    public Double[][] getDenumDataArray()
    {
        return denumDataArray;
    }

    public void setDenumDataArray( Double[][] denumDataArray )
    {
        this.denumDataArray = denumDataArray;
    }
    

   /* 
    public Map<Integer, List<Double>> numData;
    
    
    public Map<Integer, List<Double>> getNumData()
    {
        return numData;
    }

    public void setNumData( Map<Integer, List<Double>> numData )
    {
        this.numData = numData;
    }

    public Map<Integer, List<Double>> denumData;
    
    public Map<Integer, List<Double>> getDenumData()
    {
        return denumData;
    }

    public void setDenumData( Map<Integer, List<Double>> denumData )
    {
        this.denumData = denumData;
    }
*/
    private String chartTitle;

    public String getChartTitle()
    {
        return chartTitle;
    }

    public void setChartTitle( String chartTitle )
    {
        this.chartTitle = chartTitle;
    }
   
    private String xAxis_Title;

    public String getXAxis_Title()
    {
        return xAxis_Title;
    }

    public void setXAxis_Title( String axis_Title )
    {
        xAxis_Title = axis_Title;
    }
     private String yAxis_Title;

    public String getYAxis_Title()
    {
        return yAxis_Title;
    }

    public void setYAxis_Title( String axis_Title )
    {
        yAxis_Title = axis_Title;
    }

   
    
    
}    
   
