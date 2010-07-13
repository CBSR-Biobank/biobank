;NSIS Modern User Interface version 1.69
;Original templates by Joost Verburg
;Redesigned for BZFlag by blast007
;Redesigned for BioBank2 by Thomas Polasek

;--------------------------------
;BioBank2 Version Variables

  !define VERSION_STR "2.0.1.a" 
  !define EXPORTED_BIOBANK2 "BioBank2_v${VERSION_STR}_win32"

;--------------------------------
;Compression options

  ;If you want to comment these
  ;out while testing, it speeds
  ;up the installer compile time
  ;Uncomment to reduce installer
  ;size by ~35%
  
  ;SetCompress off
  SetCompress auto
  SetCompressor /SOLID lzma

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;Configuration

  ;General
  Name "BioBank ${VERSION_STR}"
  OutFile "..\BioBankInstaller-${VERSION_STR}.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\BioBank2"

  ; Make it look pretty in XP
  XPStyle on

;--------------------------------
;Variables
  Var STARTMENU_STR
  Var STARTMENU_FOLDER
  Var INSTALL_DIR_STR
  

;--------------------------------
;Interface Settings

  ;Icons
  !define MUI_ICON "biobank.ico"
  !define MUI_UNICON "uninstall.ico"

  ;Bitmaps
  !define MUI_WELCOMEFINISHPAGE_BITMAP "side.bmp"
  !define MUI_UNWELCOMEFINISHPAGE_BITMAP "side.bmp"

  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP "header.bmp"
  !define MUI_COMPONENTSPAGE_CHECKBITMAP "${NSISDIR}\Contrib\Graphics\Checks\simple-round2.bmp"

  !define MUI_COMPONENTSPAGE_SMALLDESC

  ;Show a warning before aborting install
  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  ;Welcome page configuration
  !define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of BioBank ${VERSION_STR}.\r\n\r\nBioBank is an application you must get.\r\n\r\nClick Next to continue."

  
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "licence.rtf"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKLM" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\BioBank" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  !define MUI_STARTMENUPAGE_DEFAULTFOLDER "BioBank2"

  !insertmacro MUI_PAGE_STARTMENU Application $STARTMENU_FOLDER

  !insertmacro MUI_PAGE_INSTFILES
  
  ;Finished page configuration
  !define MUI_FINISHPAGE_NOAUTOCLOSE
  !define MUI_FINISHPAGE_LINK "http://aicml-med:3000/projects/biobank2/wiki"
  !define MUI_FINISHPAGE_LINK_LOCATION "http://aicml-med:3000/projects/biobank2/wiki"
  

  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

  !define MUI_UNFINISHPAGE_NOAUTOCLOSE
  !insertmacro MUI_UNPAGE_FINISH
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"


!macro MACRO_UNINSTALL
  ; remove the directory we will be installing to. (if it exists)
  RMDir /R "$INSTDIR"

  ; remove the previous installation
  ReadRegStr $INSTALL_DIR_STR HKLM SOFTWARE\BioBank "Install_Dir"
  IfErrors +2 0
  RMDir /R "$INSTALL_DIR_STR"

  ;delete startmenu (directory stored in registry)
  ReadRegStr $STARTMENU_STR HKLM SOFTWARE\BioBank "Start Menu Folder"
  IfErrors +2 0
  RMDir /R "$SMPROGRAMS\$STARTMENU_STR"
  
  ;delete quicklaunch, desktop
  Delete "$QUICKLAUNCH\BioBank.lnk"
  Delete "$DESKTOP\BioBank.lnk"
  
   ;remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\BioBank"
  DeleteRegKey HKLM "SOFTWARE\BioBank"
  DeleteRegKey HKCU "Software\BioBank"
!macroend

;--------------------------------
;Installer Sections

Section "!BioBank Core(Required)" BioBank
  ;Make it required
  SectionIn RO
  
  
  ;If we find the BioBank key.. then uninstall the previous version of BioBank
  ReadRegStr $STARTMENU_STR HKLM SOFTWARE\BioBank "BioBank"
  IfErrors +2 0
  !insertmacro MACRO_UNINSTALL
  
  ; copy over the exported biobank directory
  SetOutPath $INSTDIR\${EXPORTED_BIOBANK2}
  File /r ..\${EXPORTED_BIOBANK2}\*
  
  ; make the doc dir
  SetOutPath $INSTDIR\${EXPORTED_BIOBANK2}\doc
  File licence.rtf
  
  ;Write biobank registry keys
  WriteRegStr HKLM SOFTWARE\BioBank "Version" "${VERSION_STR}"
  WriteRegStr HKLM SOFTWARE\BioBank "Install_Dir" "$INSTDIR"
  WriteRegStr HKLM SOFTWARE\BioBank "BioBank" "I do not fear computers. I fear the lack of them."
  
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\BioBank" "DisplayName" "BioBank (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\BioBank" "UninstallString" '"$INSTDIR\${EXPORTED_BIOBANK2}\uninstall.exe"'
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\${EXPORTED_BIOBANK2}\Uninstall.exe"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Main start menu shortcuts
    SetOutPath $INSTDIR\${EXPORTED_BIOBANK2}
    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\${EXPORTED_BIOBANK2}\uninstall.exe" "" "$INSTDIR\${EXPORTED_BIOBANK2}\uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\BioBank.lnk" "$INSTDIR\${EXPORTED_BIOBANK2}\biobank2.exe" "" "$INSTDIR\${EXPORTED_BIOBANK2}\biobank2.exe" 0
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

Section "Quick Launch Shortcuts" QuickLaunch
  ;shortcut in the "quick launch bar"
  SetOutPath $INSTDIR\${EXPORTED_BIOBANK2}
  CreateShortCut "$QUICKLAUNCH\BioBank.lnk" "$INSTDIR\${EXPORTED_BIOBANK2}\biobank2.exe" "" "$INSTDIR\${EXPORTED_BIOBANK2}\biobank2.exe" 0
SectionEnd

Section "Desktop Icon" Desktop
  ;shortcut on the "desktop"
  SetOutPath $INSTDIR\${EXPORTED_BIOBANK2}
  CreateShortCut "$DESKTOP\BioBank.lnk" "$INSTDIR\${EXPORTED_BIOBANK2}\biobank2.exe" "" "$INSTDIR\${EXPORTED_BIOBANK2}\biobank2.exe" 0
SectionEnd

;--------------------------------
;Descriptions

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${Biobank} "Install the main BioBank application"
    !insertmacro MUI_DESCRIPTION_TEXT ${QuickLaunch} "Adds a shortcut in the Quick Launch toolbar."
    !insertmacro MUI_DESCRIPTION_TEXT ${Desktop} "Adds a shortcut on the desktop."
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------


Section "Uninstall"
   !insertmacro MACRO_UNINSTALL
SectionEnd
