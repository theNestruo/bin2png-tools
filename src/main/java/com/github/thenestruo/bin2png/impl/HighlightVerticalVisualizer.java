package com.github.thenestruo.bin2png.impl;

import com.github.thenestruo.commons.Chars;
import com.github.thenestruo.commons.IntArrays;
import com.github.thenestruo.commons.maps.Pair;

public class HighlightVerticalVisualizer extends VerticalVisualizer {

	protected static final Pair<Integer, Integer> PADDING_COLORS = Pair.of(0x838482, 0x0f0f0f);
	protected static final Pair<Integer, Integer> ASCII_COLORS = Pair.of(0xB5CEA8, 0x252526);
	protected static final Pair<Integer, Integer> CALL_COLORS = Pair.of(0x569CD6, 0x264f78);
	protected static final Pair<Integer, Integer> JUMP_COLORS = Pair.of(0x569CD6, 0x252526);
	protected static final Pair<Integer, Integer> RET_COLORS = Pair.of(0x569CD6, 0x252526);
	protected static final Pair<Integer, Integer> MUTED_COLORS = Pair.of(0x434442, 0x1e1e1e);

	public HighlightVerticalVisualizer(final Integer targetHeight, final int hSpacing) {
		super(targetHeight, hSpacing);
	}

	@Override
	protected Pair<Integer, Integer> colorsFor(final byte[] buffer, final int address) {

		if (this.isPadding(buffer, address)) {
			return PADDING_COLORS;
		}

		if (this.isAscii(buffer, address)) {
			return ASCII_COLORS;
		}

		if (this.isZ80Call(buffer, address)
				|| this.isZ80Call(buffer, address + 1)
				|| this.isZ80Call(buffer, address + 2)) {
			return CALL_COLORS;
		}

		if (this.isZ80Jump(buffer, address)
				|| this.isZ80Jump(buffer, address + 1)
				|| this.isZ80Jump(buffer, address + 2)) {
			return JUMP_COLORS;
		}

		if (this.isZ80Ret(buffer, address)
			&& ((this.isZ80Call(buffer, address - 1)
					|| this.isZ80Jump(buffer, address - 1)))) {
			return RET_COLORS;
		}

		return MUTED_COLORS;
	}

	private boolean isPadding(final byte[] buffer, final int address) {

		for (int i = -2; i <= 0; i++) {
			boolean allPadding0 = true;
			boolean allPadding1 = true;
			for (int j = i; (j <= (i + 2)) && (allPadding0 || allPadding1); j++) {
				final int value = this.valueAt(buffer, address + j);
				allPadding0 &= this.isPadding0(value);
				allPadding1 &= this.isPadding1(value);
			}
			if (allPadding0 || allPadding1) {
				return true;
			}
		}
		return false;
	}

	private boolean isAscii(final byte[] buffer, final int address) {

		for (int i = -2; i <= 0; i++) {
			boolean allAscii = true;
			for (int j = i; (j <= (i + 2)) && allAscii; j++) {
				allAscii &= this.isAscii(this.valueAt(buffer, address + j));
			}
			if (allAscii) {
				return true;
			}
		}
		return false;
	}

	private boolean isZ80Call(final byte[] buffer, final int address) {

		final int[] callOpcodes = new int[] { 0xcd, 0xdc, 0xfc, 0xd4, 0xc4, 0xf4, 0xec, 0xe4, 0xcc };

		return IntArrays.contains(callOpcodes, this.valueAt(buffer, address - 2))
				&& this.isZ80CallOrJumpTarget(this.wordValueAt(buffer, address - 1));
	}

	private boolean isZ80Jump(final byte[] buffer, final int address) {

		final int[] jumpOpcodes = new int[] { 0xc3, 0xda, 0xfa, 0xd2, 0xc2, 0xf2, 0xea, 0xe2, 0xca };

		return IntArrays.contains(jumpOpcodes, this.valueAt(buffer, address - 2))
				&& this.isZ80CallOrJumpTarget(this.wordValueAt(buffer, address - 1));
	}

	private boolean isZ80Ret(final byte[] buffer, final int address) {

		final int[] retOpcodes = new int[] { 0xc9, 0xd8, 0xf8, 0xd0, 0xc0, 0xf0, 0xe8, 0xe0, 0xc8 };

		return IntArrays.contains(retOpcodes, this.valueAt(buffer, address));
	}

	private boolean isZ80CallOrJumpTarget(final int targetAddress) {

		final int[] biosAddresses = new int[] {
			// MSX BIOS
			0x0000, // CHKRAM
			0x0008, // SYNCHR
			0x000c, // RDSLT
			0x0010, // CHRGTR
			0x0014, // WRSLT
			0x0018, // OUTDO
			0x001c, // CALSLT
			0x0020, // DCOMPR
			0x0024, // ENASLT
			0x0028, // GETYPR
			0x0030, // CALLF
			0x0038, // KEYINT
			0x003b, // INITIO
			0x003e, // INIFNK
			0x0041, // DISSCR
			0x0044, // ENASCR
			0x0047, // WRTVDP
			0x004a, // RDVRM
			0x004d, // WRTVRM
			0x0050, // SETRD
			0x0053, // SETWRT
			0x0056, // FILVRM
			0x0059, // LDIRMV
			0x005c, // LDIRVM
			0x005f, // CHGMOD
			0x0062, // CHGCLR
			0x0066, // NMI
			0x0069, // CLRSPR
			0x006c, // INITXT
			0x006f, // INIT32
			0x0072, // INIGRP
			0x0075, // INIMLT
			0x0078, // SETTXT
			0x007b, // SETT32
			0x007e, // SETGRP
			0x0081, // SETMLT
			0x0084, // CALPAT
			0x0087, // CALATR
			0x008a, // GSPSIZ
			0x008d, // GRPPRT
			0x0090, // GICINI
			0x0093, // WRTPSG
			0x0096, // RDPSG
			0x0099, // STRTMS
			0x009c, // CHSNS
			0x009f, // CHGET
			0x00a2, // CHPUT
			0x00a5, // LPTOUT
			0x00a8, // LPTSTT
			0x00ab, // CNVCHR
			0x00ae, // PINLIN
			0x00b1, // INLIN
			0x00b4, // QINLIN
			0x00b7, // BREAKX
			0x00ba, // ISCNTC
			0x00bd, // CKCNTC
			0x00c0, // BEEP
			0x00c3, // CLS
			0x00c6, // POSIT
			0x00c9, // FNKSB
			0x00cc, // ERAFNK
			0x00cf, // DSPFNK
			0x00d2, // TOTEXT
			0x00d5, // GTSTCK
			0x00d8, // GTTRIG
			0x00db, // GTPAD
			0x00de, // GTPDL
			0x00e1, // TAPION
			0x00e4, // TAPIN
			0x00e7, // TAPIOF
			0x00ea, // TAPOON
			0x00ed, // TAPOUT
			0x00f0, // TAPOOF
			0x00f3, // STMOTR
			0x00f6, // LFTQ
			0x00f9, // PUTQ
			0x00fc, // RIGHTC
			0x00ff, // LEFTC
			0x0102, // UPC
			0x0105, // TUPC
			0x0108, // DOWNC
			0x010b, // TDOWNC
			0x010e, // SCALXY
			0x0111, // MAPXYC
			0x0114, // FETCHC
			0x0117, // STOREC
			0x011a, // SETATR
			0x011d, // READC
			0x0120, // SETC
			0x0123, // NSETCX
			0x0126, // GTASPC
			0x0129, // PNTINI
			0x012c, // SCANR
			0x012f, // SCANL
			0x0132, // CHGCAP
			0x0135, // CHGSND
			0x0138, // RSLREG
			0x013b, // WSLREG
			0x013e, // RDVDP
			0x0141, // SNSMAT
			0x0144, // PHYDIO
			0x0147, // FORMAT
			0x014a, // ISFLIO
			0x014d, // OUTDLP
			0x0150, // GETVCP
			0x0153, // GETVC2
			0x0156, // KILBUF
			0x0159, // CALBAS
			// MSX 2 BIOS
			0x015c, // SUBROM
			0x015f, // EXTROM
			0x0162, // CHKSLZ
			0x0165, // CHKNEW
			0x0168, // EOL
			0x016b, // BIGFIL
			0x016e, // NSETRD
			0x0171, // NSTWRT
			0x0174, // NRDVRM
			0x0177, // NWRVRM
			// MSX 2+ BIOS
			0x017a, // RDBTST
			0x017d, // WRBTST
			// MSX turbo R BIOS
			0x0180, // CHGCPU
			0x0183, // GETCPU
			0x0186, // PCMPLY
			0x0189 // PCMREC
		};

		return IntArrays.contains(biosAddresses, targetAddress)
				|| ((targetAddress >= 0x4000) && (targetAddress <= 0xbfff)); // ROM address
	}

	private boolean isPadding0(final int value) {

		return (value == 0x00);
	}

	private boolean isPadding1(final int value) {

		return (value == 0xff);
	}

	private boolean isAscii(final int value) {

		return Chars.isAsciiPrintable((char) value);
	}
}
