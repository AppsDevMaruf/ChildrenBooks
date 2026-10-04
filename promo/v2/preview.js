const { chromium } = require('playwright');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({viewport:{width:1080,height:1920}});
  await p.goto('file://' + process.cwd() + '/promo.html');
  await p.evaluate(() => window.ready);
  const times = (process.argv[2]||'0.8,2.9,4.2,5.4,7.0,11.7,14.6,17,20.8,24.2,28.5,31.8').split(',').map(Number);
  for (const t of times) { await p.evaluate(t => render(t), t); await p.screenshot({path:`prev_${t}.jpg`, type:'jpeg', quality:80}); }
  const errs = []; await b.close();
})();
