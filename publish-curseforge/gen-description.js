const fs = require('fs');
const path = require('path');
const hbs = require('handlebars');
const request = require('request');

request({
	url: 'https://addons-ecs.forgesvc.net/api/v2/addon/412013/files',
	headers: {
		'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36'
	}
}, (err, res, body) => {
	if (err) {
		console.error(err);
	} else {
		const json = JSON.parse(body);
		let ctx = {
			versions: {
				global: fs.readFileSync("../version.txt").toString('utf8').trim()
			},
			curse: {}
		};
		let platforms = {};
		fs.readdirSync('../').forEach((f) => {
			let basename = path.basename(f);
			if (basename.indexOf("platform-") == 0) {
				let name = basename.substring(9);
				let cleaned = name.replace(/-/g, '_').replace(/\./g, '');
				platforms[name] = cleaned;
				ctx.versions[cleaned] = ctx.versions.global+fs.readFileSync("../"+basename+"/version-suffix.txt").toString('utf8').trim();
			}
		});
		json.forEach((file) => {
			Object.entries(platforms).some(([name, cleanName]) => {
				if (file.fileName === "ears-"+name+"-"+ctx.versions[cleanName]+".jar") {
					ctx.curse[cleanName] = file.id.toString();
					return true;
				}
				return false;
			});
		});
		console.log(ctx);
		fs.writeFileSync('description.html', hbs.compile(fs.readFileSync('description.html.hbs').toString('utf8'))(ctx));
	}
});
