<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0"
   xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
   xmlns="http://www.opengis.net/sld"
   xmlns:ogc="http://www.opengis.net/ogc"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
   <Name>currents</Name>
   <UserStyle>
     <Title>currents</Title>
     <FeatureTypeStyle>
    <Transformation>
      <ogc:Function name="ras:DynamicColorMap">
       <ogc:Function name="parameter">
        <ogc:Literal>data</ogc:Literal>
       </ogc:Function>
       <ogc:Function name="parameter">
        <ogc:Literal>colorRamp</ogc:Literal>
        <ogc:Function name="colormap">
          <ogc:Function name="env">
                 <ogc:Literal>pallette</ogc:Literal>
                <ogc:Literal>GMT_seafloor</ogc:Literal>
               </ogc:Function>
         <ogc:Literal>-1</ogc:Literal>
         <ogc:Literal>1</ogc:Literal>
        </ogc:Function>
       </ogc:Function>
      </ogc:Function>
    </Transformation>
    <Rule>
     <Name>rule1</Name>
     <RasterSymbolizer>
       <Opacity>1.0</Opacity>
     </RasterSymbolizer>
    </Rule>
    </FeatureTypeStyle>
     <FeatureTypeStyle>
      <Transformation>
         <ogc:Function name="ras:RasterAsPointCollection">
            <ogc:Function name="parameter">
               <ogc:Literal>data</ogc:Literal>
            </ogc:Function>
            <ogc:Function name="parameter">
              <ogc:Literal>interpolation</ogc:Literal>
              <ogc:Literal>InterpolationBilinear</ogc:Literal>
            </ogc:Function>
            <ogc:Function name="parameter">
              <ogc:Literal>scale</ogc:Literal>
              <ogc:Function name="Categorize">
               <ogc:Function name="env">
                 <ogc:Literal>wms_scale_denominator</ogc:Literal>
               </ogc:Function>
                 <ogc:Literal>8</ogc:Literal>
                 <ogc:Literal>50000</ogc:Literal>
                 <ogc:Literal>4</ogc:Literal>
                 <ogc:Literal>100000</ogc:Literal>
                 <ogc:Literal>2</ogc:Literal>
                 <ogc:Literal>500000</ogc:Literal>
                 <ogc:Literal>1</ogc:Literal>
                 <ogc:Literal>1000000</ogc:Literal>
                 <ogc:Literal>0.2</ogc:Literal>
                 <ogc:Literal>5000000</ogc:Literal>
                 <ogc:Literal>0.1</ogc:Literal>
                 <ogc:Literal>10000000</ogc:Literal>
                 <ogc:Literal>0.05</ogc:Literal>
                 <ogc:Literal>20000000</ogc:Literal>
                 <ogc:Literal>0.02</ogc:Literal>
              </ogc:Function>
            </ogc:Function>
         </ogc:Function>
      </Transformation>
      <Rule>
        <Title>Heading</Title>
       <ogc:Filter>
         <ogc:PropertyIsLessThan>
           <ogc:PropertyName>UCUR</ogc:PropertyName>
           <ogc:Literal>1000</ogc:Literal>
         </ogc:PropertyIsLessThan>
       </ogc:Filter>
        <PointSymbolizer>
         <Graphic>
           <Mark>
             <WellKnownName>extshape://narrow</WellKnownName>
             <Fill>
               <CssParameter name="fill">
                <ogc:Function name="Categorize">
                  <ogc:Function name="sqrt">
                    <ogc:Add>
                     <ogc:Mul>
                       <ogc:PropertyName>UCUR</ogc:PropertyName>
                       <ogc:PropertyName>UCUR</ogc:PropertyName>
                     </ogc:Mul>
                     <ogc:Mul>
                       <ogc:PropertyName>VCUR</ogc:PropertyName>
                       <ogc:PropertyName>VCUR</ogc:PropertyName>
                     </ogc:Mul>
                    </ogc:Add>
                  </ogc:Function>
                  <ogc:Literal>#0000FF</ogc:Literal>
                  <ogc:Literal>0.3</ogc:Literal>
                  <ogc:Literal>#0033FF</ogc:Literal>
                  <ogc:Literal>0.2</ogc:Literal>
                  <ogc:Literal>#0066FF</ogc:Literal>
                  <ogc:Literal>0.1</ogc:Literal>
                  <ogc:Literal>#0099FF</ogc:Literal>
                  <ogc:Literal>0.05</ogc:Literal>
                  <ogc:Literal>#00CCFF</ogc:Literal>
                  <ogc:Literal>0.02</ogc:Literal>
                  <ogc:Literal>#00FFFF</ogc:Literal>
                </ogc:Function>
               </CssParameter>
            </Fill>
           </Mark>
           <Size>
             <ogc:Function name="Categorize">
                <!-- Value to transform -->
               <ogc:Function name="sqrt">
                 <ogc:Add>
                     <ogc:Mul>
                       <ogc:PropertyName>UCUR</ogc:PropertyName>
                       <ogc:PropertyName>UCUR</ogc:PropertyName>
                     </ogc:Mul>
                     <ogc:Mul>
                       <ogc:PropertyName>VCUR</ogc:PropertyName>
                       <ogc:PropertyName>VCUR</ogc:PropertyName>
                     </ogc:Mul>
                 </ogc:Add>
               </ogc:Function>
                  <ogc:Literal>12</ogc:Literal>
                  <ogc:Literal>0.1</ogc:Literal>
                  <ogc:Literal>15</ogc:Literal>
                  <ogc:Literal>0.2</ogc:Literal>
                  <ogc:Literal>18</ogc:Literal>
                  <ogc:Literal>0.3</ogc:Literal>
                  <ogc:Literal>24</ogc:Literal>
                  <ogc:Literal>0.4</ogc:Literal>
                  <ogc:Literal>20</ogc:Literal>
                  <ogc:Literal>0.5</ogc:Literal>
                  <ogc:Literal>36</ogc:Literal>
                  <ogc:Literal>0.6</ogc:Literal>
                  <ogc:Literal>42</ogc:Literal>
                  <ogc:Literal>0.7</ogc:Literal>
                  <ogc:Literal>48</ogc:Literal>
                  <ogc:Literal>0.8</ogc:Literal>
                  <ogc:Literal>54</ogc:Literal>
                  <ogc:Literal>0.9</ogc:Literal>
                  <ogc:Literal>60</ogc:Literal>
                  <ogc:Literal>1.0</ogc:Literal>
                  <ogc:Literal>66</ogc:Literal>
                  <ogc:Literal>1.1</ogc:Literal>
                  <ogc:Literal>75</ogc:Literal>
                </ogc:Function>
           </Size>
           <Rotation>
               <ogc:Function name="toDegrees">
                <ogc:Function name="atan2">
                   <ogc:PropertyName>UCUR</ogc:PropertyName>
                   <ogc:PropertyName>VCUR</ogc:PropertyName>
                </ogc:Function>
               </ogc:Function>
           </Rotation>
         </Graphic>
        </PointSymbolizer>
      </Rule>
     </FeatureTypeStyle>
   </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>