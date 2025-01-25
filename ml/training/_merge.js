const fs = require('fs');
const path = require('path');
const files = fs.readdirSync(__dirname);
console.log(`found ${files.length} files`)
let result = "[\n";
for (const file of files) {
    if (!file.endsWith(".json")) {  // just json files
        console.log("skipping " + file);
        continue;
    }
    const filepath = path.join(__dirname, file);
    console.log("reading " + filepath);
    const contents = fs.readFileSync(filepath, 'utf8');
    const json = JSON.parse(contents);
    for (const input in json) {
        result += `  { "inputs": ${JSON.stringify([input])}, "outputs": ${[JSON.stringify([JSON.stringify(json[input])])]} },\n`;
    }
}
result = result.slice(0, -2) + "\n]"
console.log(result);
fs.writeFileSync(path.join(__dirname, "output", "merged.json"), result);
