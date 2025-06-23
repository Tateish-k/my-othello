# マイオセロ
## 概要
KotlinとJetpack Compose for Desktopで開発されたオセロゲーム
![image](https://github.com/user-attachments/assets/52437e13-f2b3-4736-b492-28b1030e9944)

## 特徴
- **2つのプレイモード**
  - 対人モード（2人で交互にプレイ）
  - AI対戦モード（6段階の難易度選択）
- **カスタマイズ可能なインターフェース**
  - ボードの色テーマ変更
  - 駒のスタイル選択
- **ゲーム進行支援機能**
  - 有効な手の場所をハイライト表示
  - 石のカウント表示
 
## インストールと実行

### 前提条件
- Java 17以上

### 実行手順

1. リポジトリのクローン
 - git clone https://github.com/Tateish-k/my-othello.git

2. プロジェクトディレクトリへ移動
 - cd my-othello
 - cd KotlinProject

3. 依存関係の解決とビルド
 - ./gradlew build

4. アプリケーションの実行
 - ./gradlew run
