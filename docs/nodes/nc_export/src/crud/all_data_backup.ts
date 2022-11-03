    // @ts-nocheck
export function exportAllDataSql(){
    const USER="postgres"
    const DBNAME="boozsoft-nc"
    const PASSWORD="Sigoo@@123"
// run_backup.js
    const execFile = require('child_process').execFile;
    const date = new Date();
    const current_date = `${date.getFullYear()}-${date.getMonth() +
    1}-${date.getDate()}-${date.getHours()}-${date.getMinutes()}`;
    const backup_file = `export_${current_date}.sql`;
    const backup_file_ext = `export_${current_date}.tar`;

    let backup_script = `pg_dump  -c -x --inserts --enable-row-security  -h 81.70.32.227 --port=5433 --username=${USER} ${DBNAME} --no-owner  `;

    var script = execFile(
        __dirname+`/backup.sh`,
        [backup_script, backup_file, PASSWORD],
        (error, stdout, stderr) => {
            if (error !== null) {
                console.log(`exec error: ${error}`);
            }
            console.log('Backup complete!')
        }
    );

}
