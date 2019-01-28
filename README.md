# Bluetooth_Sample

### 說明
一個簡單的藍牙連接配對範例配對完成後帶出裝置訊息.

### 環境
<ul>
 <li>Android 3.3 version
 <li>兩隻 Samsung Android 5手機
 <li>compileSdkVersion 28
 <li>minSdkVersion 19
 <li>targetSdkVersion 28
</ul>

### 流程
<ul>
 <li>點擊螢幕後開始掃描環境並且做權限驗證
 <li>掃描周圍環境後顯示裝置列表
</ul>

<img alt="Start discovery" src="/assets/screenshots/1.png" height="600"/> <img alt="Turning on Bluetooth" src="/assets/screenshots/2.png" height="600"/>

<ul>
 <li>點擊列表項目開始配對
 <li>配對完成後顯示裝置資訊
</ul>

<img alt="Device found" src="/assets/screenshots/3.png" height="600"/> <img alt="Pairing started" src="/assets/screenshots/4.png" height="600"/>

### 備註
從Android 3.2使用com.android.support:appcompat-v7依賴套件升級到開發環境使用Android 3.3時若遭遇IDE相容性問題請將gradle.properties加上

<ul>
  <li>android.useAndroidX=true
  <li>android.enableJetifier=true
</ul>

即可使用AndroidX&之前的開發相關可不用Migrate
