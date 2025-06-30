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
    this.signaturePad = new SignaturePad(this.canvasRef.nativeElement);
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
