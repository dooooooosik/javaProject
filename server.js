const express = require("express");
const path = require("path");
const app = express();
const PORT = 5500;

// 정적 파일 제공 설정
app.use("/css", express.static(path.join(__dirname, "frontDream", "frontCSS"))); // CSS 경로
app.use("/img", express.static(path.join(__dirname, "frontDream", "img"))); // 이미지 경로
app.use("/frontHTML", express.static(path.join(__dirname, "frontDream", "frontHTML"))); // HTML 경로

// 기본 경로 처리
app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "frontDream", "frontHTML", "mainPage.html")); // 기본 페이지 설정
});

// 서버 실행
app.listen(PORT, () => {
  console.log(`Server is running on http://127.0.0.1:${PORT}`);
});