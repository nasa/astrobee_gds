/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.ui3d.font;

import com.ardor3d.extension.ui.text.TextFactory;

public class FontInitializer {

    public static void initialize() {
        final VerveFontProvider fontProvider = new VerveFontProvider();
        //-- Droid Sans --------------------------------
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-12-bold-italic", "DroidSans", 12, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-12-bold-regular", "DroidSans", 12, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-12-medium-italic", "DroidSans", 12, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-12-medium-regular", "DroidSans", 12, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-14-bold-italic", "DroidSans", 14, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-14-bold-regular", "DroidSans", 14, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-14-medium-italic", "DroidSans", 14, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-14-medium-regular", "DroidSans", 14, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-16-bold-italic", "DroidSans", 16, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-16-bold-regular", "DroidSans", 16, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-16-medium-italic", "DroidSans", 16, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-16-medium-regular", "DroidSans", 16, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-18-bold-italic", "DroidSans", 18, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-18-bold-regular", "DroidSans", 18, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-18-medium-italic", "DroidSans", 18, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-18-medium-regular", "DroidSans", 18, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-22-bold-italic", "DroidSans", 22, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-22-bold-regular", "DroidSans", 22, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-22-medium-italic", "DroidSans", 22, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSans-22-medium-regular", "DroidSans", 22, false, false);

        //-- Droid Sans Outlined  ----------
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-12-bold-italic", "DroidSansOL", 12, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-12-bold-regular", "DroidSansOL", 12, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-12-medium-italic", "DroidSansOL", 12, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-12-medium-regular", "DroidSansOL", 12, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-14-bold-italic", "DroidSansOL", 14, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-14-bold-regular", "DroidSansOL", 14, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-14-medium-italic", "DroidSansOL", 14, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-14-medium-regular", "DroidSansOL", 14, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-16-bold-italic", "DroidSansOL", 16, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-16-bold-regular", "DroidSansOL", 16, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-16-medium-italic", "DroidSansOL", 16, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-16-medium-regular", "DroidSansOL", 16, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-18-bold-italic", "DroidSansOL", 18, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-18-bold-regular", "DroidSansOL", 18, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-18-medium-italic", "DroidSansOL", 18, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-18-medium-regular", "DroidSansOL", 18, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-22-bold-italic", "DroidSansOL", 22, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-22-bold-regular", "DroidSansOL", 22, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-22-medium-italic", "DroidSansOL", 22, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-22-medium-regular", "DroidSansOL", 22, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-24-bold-italic", "DroidSansOL", 24, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-24-bold-regular", "DroidSansOL", 24, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-24-medium-italic", "DroidSansOL", 24, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-24-medium-regular", "DroidSansOL", 24, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-26-bold-italic", "DroidSansOL", 26, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-26-bold-regular", "DroidSansOL", 26, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-26-medium-italic", "DroidSansOL", 26, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-26-medium-regular", "DroidSansOL", 26, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-28-bold-italic", "DroidSansOL", 28, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-28-bold-regular", "DroidSansOL", 28, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-28-medium-italic", "DroidSansOL", 28, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-28-medium-regular", "DroidSansOL", 28, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-30-bold-italic", "DroidSansOL", 30, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-30-bold-regular", "DroidSansOL", 30, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-30-medium-italic", "DroidSansOL", 30, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DroidSansOL-30-medium-regular", "DroidSansOL", 30, false, false);
        
        //-- DejaVuMono --------------------
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-12-bold-italic", "DejaVuMono", 12, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-12-bold-regular", "DejaVuMono", 12, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-12-medium-italic", "DejaVuMono", 12, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-12-medium-regular", "DejaVuMono", 12, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-14-bold-italic", "DejaVuMono", 14, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-14-bold-regular", "DejaVuMono", 14, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-14-medium-italic", "DejaVuMono", 14, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-14-medium-regular", "DejaVuMono", 14, false, false);
        
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-16-bold-italic", "DejaVuMono", 16, true, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-16-bold-regular", "DejaVuMono", 16, true, false);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-16-medium-italic", "DejaVuMono", 16, false, true);
        fontProvider.addFont("gov/nasa/arc/verve/ui3d/font/DejaVuMono-16-medium-regular", "DejaVuMono", 16, false, false);
        
        TextFactory.INSTANCE.setFontProvider(fontProvider);


    }
}
