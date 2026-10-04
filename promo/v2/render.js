const { chromium } = require('playwright');
const fs = require('fs');
(async () => {
  const FPS = 30, DUR = 33;
  fs.mkdirSync('frames', {recursive:true});
  const b = await chromium.launch();
  const p = await b.newPage({viewport:{width:1080,height:1920}});
  await p.goto('file://' + process.cwd() + '/promo.html');
  await p.evaluate(() => window.ready);
  const n = FPS*DUR; const t0 = Date.now();
  for (let i=0;i<n;i++){
    await p.evaluate(t => render(t), i/FPS);
    await p.screenshot({path:`frames/f${String(i).padStart(5,'0')}.jpg`, type:'jpeg', quality:93});
    if (i%150==0) console.log(i, ((Date.now()-t0)/1000).toFixed(1)+'s');
  }
  await b.close();
})();
