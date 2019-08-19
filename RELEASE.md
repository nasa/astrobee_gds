# Astrobee Control Station

## Release 0.1.0 - August 16, 2019
- **You must delete the ControlStationConfig folder after updating to this version** because of these changes:
  - Refactored names of config files
  - Added ISS configuration file to GraniteLab world folder (to fix bug that prevented Plans made with ISS version from
  opening in Granite Lab version)
- Put Grab Control, Station Keep, and Stop Recording buttons in TopBar
- Renamed Advanced tab to Engineering and Advanced 2 tab to Other
- Added Camera Mode (Recording, Streaming, Both) param to Set Camera commands/files
- Health and Status subtab:
  - Display DataToDisk recording state and name of recording profile
- Teleoperate Tab:
  - Added multiple input lines on Relative Commanding Text tab (renamed from Relative Commanding)
  - Added interactive Relative Commanding tab that works with preview model like Bee Commanding tab
  - Added capability to create Bookmark at position of teleop preview
- Engineering Tab (was Advanced Tab):
  - Added Localization Commands part
  - Moved DataToDisk and DataToGds to this tab
  - DataToDiskPart:
    - Added "favorites" options
    - Added Start and Stop Recording buttons
    - Made tables sortable
    - Display recording state and name of recording profile
  - DataToGds part:
    - Added frequency for cpu state, gnc state, and pmc cmd state
    - Added scrollbar
- Other Tab (was Advanced2 Tab):
  - Moved DataSize and CameraStreaming displays to this tab
- Plan Editor Tab:
  - Added Plan Preview capability
  - Added PlanCommands for setCheckObstacles, initializeBias, setCheckZones, setHolonomicMode, setPlanner,
   setTelemetryRate, switchLocalization, startRecording, and stopRecording
- Fixed Bsharp showing as yellow model
- Updated astrobee_common idls to July 17, 2019
- Added separate window to display raw EKF and GNC telemetry (access through View -> Numbers Window)
- Various bug fixes and updates to documentation

## Release 0.0.3 - July 9, 2019
- Fixed links in documentation.

## Release 0.0.2 - July 5, 2019
- Fixed formatting and links in documentation.

## Release 0.0.1 - June 26, 2019
- Augmented usage documentation with references to code files, etc.
