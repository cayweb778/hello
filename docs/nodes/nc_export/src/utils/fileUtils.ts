// @ts-nocheck

import fs from 'fs';

const util = require('util');
export const readAsync = util.promisify(fs.readFile);
