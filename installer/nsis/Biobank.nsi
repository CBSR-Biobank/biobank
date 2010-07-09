;NSIS Modern User Interface version 1.69
;Original templates by Joost Verburg
;Redesigned for BZFlag by blast007
;Redesigned for Biobank2 by Thomas Polasek

;--------------------------------
;Biobank2 Version Variables

  !define VER_MAJOR 2.0
  !define VER_MINOR .1

;--------------------------------
;Compression options

  ;If you want to comment these
  ;out while testing, it speeds
  ;up the installer compile time
  ;Uncomment to reduce installer
  ;size by ~35%
  
  ;TODO uncomment
  ;SetCompress auto
  ;SetCompressor /SOLID lzma
  
  SetCompress off

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;Configuration

  ;General
  Name "Biobank ${VER_MAJOR}${VER_MINOR}"
  OutFile "..\..\Biobank-${VER_MAJOR}${VER_MINOR}.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\Biobank\Biobank_v${VER_MAJOR}${VER_MINOR}_win32"

  ; Make it look pretty in XP
  XPStyle on

;--------------------------------
;Variables
  Var STARTMENU_STR
  Var STARTMENU_FOLDER
  

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
  !define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of Biobank ${VER_MAJOR}${VER_MINOR}.\r\n\r\nBiobank is an application you must get.\r\n\r\nClick Next to continue."

  
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "licence.rtf"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKLM" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\Biobank" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"

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
  ; remove directories used.
  RMDir /R "$INSTDIR"
  
  ;delete startmenu (directory stored in registry)
  ReadRegStr $STARTMENU_STR HKLM SOFTWARE\Biobank "Start Menu Folder"
  IfErrors +2 0
  RMDir /R "$SMPROGRAMS\$STARTMENU_STR"
  
  ;delete quicklaunch, desktop
  Delete "$QUICKLAUNCH\Biobank.lnk"
  Delete "$DESKTOP\Biobank.lnk"
  
   ;remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Biobank"
  DeleteRegKey HKLM "SOFTWARE\Biobank"
  DeleteRegKey HKCU "Software\Biobank"
!macroend

;--------------------------------
;Installer Sections

Section "!Biobank Core(Required)" Biobank
  ;Make it required
  SectionIn RO
  
  ReadRegStr $STARTMENU_STR HKLM SOFTWARE\Biobank "Biobank"
  IfErrors +2 0
  !insertmacro MACRO_UNINSTALL
  
  ; make the eclipse dir
  SetOutPath $INSTDIR\eclipse
  File /r ..\eclipse\*
  
   ; make the respository dir
  SetOutPath $INSTDIR\repository
  File /r ..\repository\*
  
  ; make the doc dir
  SetOutPath $INSTDIR\doc
  File licence.rtf
  
  ;Write biobank registry keys
  WriteRegStr HKLM SOFTWARE\Biobank "Version" "${VER_MAJOR}${VER_MINOR}"
  WriteRegStr HKLM SOFTWARE\Biobank "Install_Dir" "$INSTDIR"
  WriteRegStr HKLM SOFTWARE\Biobank "Biobank" "I do not fear computers. I fear the lack of them."
  
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Biobank" "DisplayName" "Biobank (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Biobank" "UninstallString" '"$INSTDIR\uninstall.exe"'
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Main start menu shortcuts
    SetOutPath $INSTDIR
    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Biobank-${VER_MAJOR}${VER_MINOR}.lnk" "$INSTDIR\eclipse\biobank2.exe" "" "$INSTDIR\eclipse\biobank2.exe" 0
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

Section "Quick Launch Shortcuts" QuickLaunch
  ;shortcut in the "quick launch bar"
  SetOutPath $INSTDIR
  CreateShortCut "$QUICKLAUNCH\Biobank.lnk" "$INSTDIR\eclipse\biobank2.exe" "" "$INSTDIR\eclipse\biobank2.exe" 0
SectionEnd

Section "Desktop Icon" Desktop
  ;shortcut on the "desktop"
  SetOutPath $INSTDIR
  CreateShortCut "$DESKTOP\Biobank.lnk" "$INSTDIR\eclipse\biobank2.exe" "" "$INSTDIR\eclipse\biobank2.exe" 0
SectionEnd

;--------------------------------
;Descriptions

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${Biobank} "Install the main Biobank application"
    !insertmacro MUI_DESCRIPTION_TEXT ${QuickLaunch} "Adds a shortcut in the Quick Launch toolbar."
    !insertmacro MUI_DESCRIPTION_TEXT ${Desktop} "Adds a shortcut on the desktop."
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section



Section "Uninstall"
   !insertmacro MACRO_UNINSTALL
SectionEnd
