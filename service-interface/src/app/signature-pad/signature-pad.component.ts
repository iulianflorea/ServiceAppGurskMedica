import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import SignaturePad from "signature_pad";

@Component({
  selector: 'app-signature-pad',
  templateUrl: './signature-pad.component.html',
  styleUrls: ['./signature-pad.component.css']
})
export class SignaturePadComponent implements AfterViewInit {
  @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  signaturePad!: SignaturePad;

  ngAfterViewInit(): void {
    const canvas = this.canvasRef.nativeElement;
    if (window.innerWidth <= 600) {
      canvas.width = Math.min(window.innerWidth - 48, 320);
      canvas.height = 180;
    } else {
      canvas.width = 420;
      canvas.height = 200;
    }
    this.signaturePad = new SignaturePad(canvas, { penColor: 'black' });
  }

  clear(): void {
    this.signaturePad.clear();
  }

  getSignatureImage(): string {
    return this.signaturePad.toDataURL(); // base64 PNG
  }

  isEmpty(): boolean {
    return this.signaturePad.isEmpty();
  }
}
