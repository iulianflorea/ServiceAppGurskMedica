import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import {Capacitor} from "@capacitor/core";
import {StatusBar} from "@capacitor/status-bar";


platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));


if (Capacitor.getPlatform() === 'android') {
  StatusBar.hide().catch((err) => console.warn('StatusBar hide error', err));
}
