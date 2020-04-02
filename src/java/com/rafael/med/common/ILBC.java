package com.rafael.med.common;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class ILBC implements Codec
{

	// ------------------------------ only primitivies
	// ------------------------------------//
	public static final class InterpolateSamplesVariables
	{
		int n, highIndex, lowIndex;
	}

	public static final class ChebushevVariables
	{
		int n, b2;
		short b1Hi, b1Low;
		int temp, temp2;
	}

	public static final class CbMemEnergyAugmentationVariables
	{
		int en1;
		int currIndex;
		int currValue;
		int n;
	}

	public static final class CorrData
	{
		int correlation;
		int energy;
	}

	public static final class GetLspPolyVariables
	{
		int j, k;
		short xHi, xLow;
	}

	public static final class EnhanceUpSampleVariables
	{
		int j, pu1, pu11, ps, pp;
	}

	public static final class LspToLsfVariables
	{
		int i;
		int j;
		int currValue;
		int currLspIndex;
		int currLsfIndex;
	}

	public static final class CbSearchData
	{
		int critNew;
		short indexNew;
		short critNewSh;
	}

	public static final class CrossCorrelationVariables
	{
		int i, j;
	}

	public static final class HpOutputVariables
	{
		int i;
		int temp, temp2, tempShift;
	}

	public static final class AutoCorrelationVariables
	{
		int i, j, nBits, scale, currIndex1, currIndex2;
		short max, tempS;
	}

	public static final class XCorrCoefVariables
	{
		short max, energyScale, totScale, scaleDiff, crossCorrSqMod, energyMod, crossCorrMod, crossCorrScale;
		short energyModMax;
		short totScaleMax;
		short crossCorrSqModMax;
		short maxLag;
		int pos;
		int tempIndex1, tempIndex2, tempIndex3, tempIndex4, shifts, newCrit, maxCrit, k, energy, temp, crossCorr;
	}

	public static final class Lsf2LspVariables
	{
		int j;
		short tempS, tempS2;
	}

	public static final class Vq4Variables
	{
		int minValue;
		int i, j, temp;
		short tempS;
		int currIndex;
	}

	public static final class Vq3Variables
	{
		int minValue;
		int i, j, temp;
		short tempS;
		int currIndex;
	}

	public static final class CbSearchCoreVariables
	{
		int n, current, nBits, maxCrit;
		short max, tempS;
	}

	public static final class CbMemEnergyVariables
	{
		int currValue;
	}

	public static final class CbUpdateIndexData
	{
		int critMax;
		short shTotMax;
		short bestIndex;
		short bestGain;
	}

	public static final class SmoothVariables
	{
		short maxTotal, scale, scale1, scale2, A, B, C, denom16, w11Prim, max1, error;
		short bitsW00, bitsW10, bitsW11;
		int w11W00, w10W10, w00W00, w00, w10, w11, w00Prim, w10Prim, w11DivW00, i;
		int B32, denom, num, errors, crit, endiff;
		int tempIndex1, tempIndex2;
	}

	public static final class UpdateBestIndexVariables
	{
		int shOld, shNew;
		int gain;
		short tempShort, tempScale;
		int current;
	}

	public static final class InterpolateVariables
	{
		int k;
		short tempS;
	}

	public static final class LsfCheckVariables
	{
		int n, k;
		int currIndex1, currIndex2;
	}

	/**************************************
	 * ------------------- with arrays --------------------------------
	 */

	public static final class AbsQuantVariables
	{
		short[] quantLen = new short[2];
		short[] syntOutBuf = new short[68];
		short[] inWeightedVec = new short[68];
		short[] inWeighted = inWeightedVec;

		AbsQuantLoopVariables absQuantLoopVariables = new AbsQuantLoopVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, quantLen, 0, 2);
			System.arraycopy(ILBC.emptyArray, 0, syntOutBuf, 0, 68);
			System.arraycopy(ILBC.emptyArray, 0, inWeightedVec, 0, 68);
		}
	}

	public static final class CbConstructVariables
	{
		short[] cbIndex;
		short[] gainIndex;

		short gain[] = new short[3];
		short cbVec0[] = new short[40];
		short cbVec1[] = new short[40];
		short cbVec2[] = new short[40];

		int i;

		GetCbVecVariables getCbVecVariables = new GetCbVecVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, gain, 0, 3);
			System.arraycopy(ILBC.emptyArray, 0, cbVec0, 0, 40);
			System.arraycopy(ILBC.emptyArray, 0, cbVec1, 0, 40);
			System.arraycopy(ILBC.emptyArray, 0, cbVec2, 0, 40);
		}
	}

	public static final class CbSearchVariables
	{
		short[] gains = new short[4];
		short[] cbBuf = new short[161];
		short[] energyShifts = new short[256];
		short[] targetVec = new short[50];
		short[] cbVectors = new short[147];
		short[] codedVec = new short[40];
		short[] interpSamples = new short[80];
		short[] interSamplesFilt = new short[80];
		short[] energy = new short[256];
		short[] augVec = new short[256];

		short[] cbIndex;
		short[] gainIndex;

		short[] inverseEnergy = energy;
		short[] inverseEnergyShifts = energyShifts;
		short[] buf = cbBuf;
		short[] target = targetVec;

		int[] cDot = new int[128];
		int[] crit = new int[128];

		short[] pp;
		int ppIndex, sInd, eInd, targetEner, codedEner, gainResult;
		int stage, i, j, nBits;
		short scale, scale2;
		short range, tempS, tempS2;

		short baseSize;
		int numberOfZeroes;
		int currIndex;

		InterpolateSamplesVariables interpolateSamplesVariables = new InterpolateSamplesVariables();
		CbMemEnergyAugmentationVariables cbMemEnergyAugmentationVariables = new CbMemEnergyAugmentationVariables();
		CbMemEnergyVariables cbMemEnergyVariables = new CbMemEnergyVariables();
		CrossCorrelationVariables crossCorrelationVariables = new CrossCorrelationVariables();
		CbSearchCoreVariables cbSearchCoreVariables = new CbSearchCoreVariables();
		CreateAugmentVectorVariables createAugmentVectorVariables = new CreateAugmentVectorVariables();
		GainQuantVariables gainQuantVariables = new GainQuantVariables();
		UpdateBestIndexVariables updateBestIndexVariables = new UpdateBestIndexVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, gains, 0, 4);
			System.arraycopy(ILBC.emptyArray, 0, cbBuf, 0, 161);
			System.arraycopy(ILBC.emptyArray, 0, energyShifts, 0, 256);
			System.arraycopy(ILBC.emptyArray, 0, targetVec, 0, 50);
			System.arraycopy(ILBC.emptyArray, 0, cbVectors, 0, 147);
			System.arraycopy(ILBC.emptyArray, 0, codedVec, 0, 40);
			System.arraycopy(ILBC.emptyArray, 0, interpSamples, 0, 80);
			System.arraycopy(ILBC.emptyArray, 0, interSamplesFilt, 0, 80);
			System.arraycopy(ILBC.emptyArray, 0, energy, 0, 256);
			System.arraycopy(ILBC.emptyArray, 0, augVec, 0, 256);

			System.arraycopy(ILBC.emptyIntArray, 0, cDot, 0, 128);
			System.arraycopy(ILBC.emptyIntArray, 0, crit, 0, 128);
		}
	}

	public static final class CreateAugmentVectorVariables
	{
		short[] cbVecTmp = new short[4];
		int currIndex;

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, cbVecTmp, 0, 4);
		}
	}

	public static final class DecodeInterpolateLsfVariables
	{
		short[] lp = new short[11];
		int len;
		short s;

		LspInterpolate2PolyDecVariables lspInterpolate2PolyDecVariables = new LspInterpolate2PolyDecVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lp, 0, 11);
		}
	}

	public static final class DecodeResidualVariables
	{
		short[] reverseDecresidual;
		short[] memVec;

		int i, subCount, subFrame;
		short startPos, nBack, nFor, memlGotten;
		short diff;

		StateConstructVariables stateConstructVariables = new StateConstructVariables();
		CbConstructVariables cbConstructVariables = new CbConstructVariables();
	}

	public static final class SimpleLsfDeqVariables
	{
		int cbIndex;
		int i, j;

		public void reset()
		{
			cbIndex = 0;
		}
	}

	public static final class AbsQuantLoopVariables
	{
		short[] idxVec;
		int startIndex, currIndex;
		int i, j, k;
		int temp, temp2;
	}

	public static final class GainQuantVariables
	{
		int scale, cbIndex, n, temp, temp2, nBits;
		short[] cb;
	}

	public static final class EncoderBits
	{
		short[] LSF = new short[6];
		short[] cbIndex = new short[15];
		short[] gainIndex = new short[15];
		short[] idxVec = new short[58];
		short firstBits = 0;
		short startIdx = 0;
		short idxForMax = 0;
		boolean stateFirst = false;
	}

	public static final class HpInputVariables
	{
		short[] ba;
		short[] y;
		short[] x;

		int currIndex;
		int current;
		int i;
	}

	public static final class SplitVqVariables
	{
		Vq3Variables vq3Variables = new Vq3Variables();
		Vq4Variables vq4Variables = new Vq4Variables();
	}

	public static final class DoThePlcVariables
	{
		short[] randVec = new short[240];
		short scale, scale1, scale2, totScale;
		short shift1, shift2, shift3, shiftMax;
		short useGain, totGain, maxPerSquare, pitchFact, useLag, randLag, pick, crossSquareMax, crossSquare, tempS, tempS2, lag, max, denom, nom, corrLen;

		int temp, temp2, tempShift, i, energy, j, ind, measure, maxMeasure;
		CorrData tempCorrData = new CorrData(), corrData = new CorrData();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, randVec, 0, 240);
		}
	}

	public static final class EnhancerVariables
	{
		int newBlocks, startPos, plcBlock, iBlock, targetIndex, regressorIndex, i, corrSh, enerSh, index, pos, temp1, temp2, tempIndex, increment, window, syntIndex, ener, start, stop;
		int lag = 20, tLag = 20;
		int inputLength;
		short max16, max, shifts, scale, scale1, sqrtEnChange, sh;

		short[] enhancementBuffer;
		short[] enhancementPeriod;

		short[] downsampled = new short[180];
		short[] surround = new short[80];
		short[] lagMax = new short[3];
		short[] corr16 = new short[3];
		short[] en16 = new short[3];
		short[] totSh = new short[3];
		int[] corr32 = new int[50];
		int[] corrMax = new int[3];

		CrossCorrelationVariables crossCorrelationVariables = new CrossCorrelationVariables();
		HpOutputVariables hpOutputVariables = new HpOutputVariables();
		GetSyncSeqVariables getSyncSeqVariables = new GetSyncSeqVariables();
		SmoothVariables smoothVariables = new SmoothVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, downsampled, 0, 180);
			System.arraycopy(ILBC.emptyArray, 0, surround, 0, 80);
			System.arraycopy(ILBC.emptyArray, 0, lagMax, 0, 3);
			System.arraycopy(ILBC.emptyArray, 0, corr16, 0, 3);
			System.arraycopy(ILBC.emptyArray, 0, en16, 0, 3);
			System.arraycopy(ILBC.emptyArray, 0, totSh, 0, 3);

			System.arraycopy(ILBC.emptyIntArray, 0, corr32, 0, 50);
			System.arraycopy(ILBC.emptyIntArray, 0, corrMax, 0, 3);
		}
	}

	public static final class FrameClassifyVariables
	{
		int[] ssqEn = new int[5];

		short max, tempS;
		int n;
		short scale;
		int ssqIndex;
		int currIndex;

		public void reset()
		{
			System.arraycopy(ILBC.emptyIntArray, 0, ssqEn, 0, 5);
		}
	}

	public static final class GetCbVecVariables
	{
		short tempBuffer[] = new short[45];
		int baseSize;
		int k;

		CreateAugmentVectorVariables createAugmentVectorVariables = new CreateAugmentVectorVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, tempBuffer, 0, 45);
		}
	}

	public static final class GetSyncSeqVariables
	{
		int i, centerEndPos, q;
		short[] lagBlock = new short[7];
		short[] blockStartPos = new short[7];
		short[] plocs2 = new short[8];

		int tempIndex1, tempIndex2;

		NearestNeighborVariables nearestNeighborVariables = new NearestNeighborVariables();
		RefinerVariables refinerVariables = new RefinerVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lagBlock, 0, 7);
			System.arraycopy(ILBC.emptyArray, 0, blockStartPos, 0, 7);
			System.arraycopy(ILBC.emptyArray, 0, plocs2, 0, 8);
		}
	}

	public static final class LevinsonDurbinVariables
	{
		short[] rHi = new short[21];
		short[] rLow = new short[21];
		short[] aHi = new short[21];
		short[] aLow = new short[21];
		short[] aUpdHi = new short[21];
		short[] aUpdLow = new short[21];

		int nBits;
		int alphaExp;
		int temp, temp2, temp3;
		int i, j;
		short tempS, tempS2;
		short yHi, yLow, xHi, xLow;

		int currIndex;

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, rHi, 0, 21);
			System.arraycopy(ILBC.emptyArray, 0, rLow, 0, 21);
			System.arraycopy(ILBC.emptyArray, 0, aHi, 0, 21);
			System.arraycopy(ILBC.emptyArray, 0, aLow, 0, 21);
			System.arraycopy(ILBC.emptyArray, 0, aUpdHi, 0, 21);
			System.arraycopy(ILBC.emptyArray, 0, aUpdLow, 0, 21);
		}
	}

	public static final class Lsf2PolyVariables
	{
		int k;

		int[] f1 = new int[6];
		int[] f2 = new int[6];

		short[] lsp = new short[10];

		Lsf2LspVariables lsf2LspVariables = new Lsf2LspVariables();
		GetLspPolyVariables getLspPolyVariables = new GetLspPolyVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lsp, 0, 10);
			System.arraycopy(ILBC.emptyIntArray, 0, f1, 0, 6);
			System.arraycopy(ILBC.emptyIntArray, 0, f2, 0, 6);
		}
	}

	public static final class LsfInterpolate2PolyEncVariables
	{
		short[] lsfTemp = new short[10];

		InterpolateVariables interpolateVariables = new InterpolateVariables();
		Lsf2PolyVariables lsf2PolyVariables = new Lsf2PolyVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lsfTemp, 0, 10);
		}
	}

	public static final class LspInterpolate2PolyDecVariables
	{
		short[] lsfTemp = new short[10];

		InterpolateVariables interpolateVariables = new InterpolateVariables();
		Lsf2PolyVariables lsf2PolyVariables = new Lsf2PolyVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lsfTemp, 0, 10);
		}
	}

	public static final class NearestNeighborVariables
	{
		int i;
		short diff;
		int[] crit = new int[8];

		public void reset()
		{
			System.arraycopy(ILBC.emptyIntArray, 0, crit, 0, 8);
		}
	}

	public static final class LpcEncodeVariables
	{
		short[] lsf = new short[20];
		short[] lsfDeq = new short[20];

		SimpleLpcAnalysisVariables simpleLpcAnalysisVariables = new SimpleLpcAnalysisVariables();
		SimpleLsfQVariables simpleLsfQVariables = new SimpleLsfQVariables();
		LsfCheckVariables lsfCheckVariables = new LsfCheckVariables();
		SimpleInterpolateLsfVariables simpleInterpolateLsfVariables = new SimpleInterpolateLsfVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lsf, 0, 20);
			System.arraycopy(ILBC.emptyArray, 0, lsfDeq, 0, 20);
		}
	}

	public static final class PackBitsVariables
	{
		int resIndex, idxVecIndex;
		int i, k;

		short[] lsf;
		short[] cbIndex;
		short[] gainIndex;
		short[] idxVec;
	}

	public static final class Poly2LsfVariables
	{
		short[] lsp = new short[10];

		Poly2LspVariables poly2LspVariables = new Poly2LspVariables();
		LspToLsfVariables lspToLsfVariables = new LspToLsfVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lsp, 0, 10);
		}
	}

	public static final class Poly2LspVariables
	{
		short[] f1 = new short[6];
		short[] f2 = new short[6];

		short xMid, xLow, xHi, yMid, yLow, yHi, x, y;
		int temp;
		int i, j;
		int nBits;

		int aLowIndex;
		int aHighIndex;

		short[] current;
		int currIndex;
		int foundFreqs;

		ChebushevVariables chebushevVariables = new ChebushevVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, f1, 0, 6);
			System.arraycopy(ILBC.emptyArray, 0, f2, 0, 6);
		}
	}

	public static final class RefinerVariables
	{
		short estSegPosRounded, searchSegStartPos, searchSegEndPos, corrDim;
		short tLoc, tLoc2, st, en, fraction, max, scale;
		int i, maxTemp, scaleFact;

		short[] filt = new short[7];
		int[] corrVecUps = new int[20];
		int[] corrVecTemp = new int[5];
		short[] vect = new short[86];
		short[] corrVec = new short[5];

		int filterStateIndex, polyIndex;

		CrossCorrelationVariables crossCorrelationVariables = new CrossCorrelationVariables();
		EnhanceUpSampleVariables enhanceUpSampleVariables = new EnhanceUpSampleVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, filt, 0, 7);
			System.arraycopy(ILBC.emptyArray, 0, vect, 0, 86);
			System.arraycopy(ILBC.emptyArray, 0, corrVec, 0, 5);

			System.arraycopy(ILBC.emptyIntArray, 0, corrVecUps, 0, 20);
			System.arraycopy(ILBC.emptyIntArray, 0, corrVecTemp, 0, 5);
		}
	}

	public static final class SimpleInterpolateLsfVariables
	{
		short[] lsfOld;
		short[] lsfDeqOld;

		short[] lp = new short[11];

		int step;
		int index;
		int i;

		LsfInterpolate2PolyEncVariables lsfInterpolate2PolyEncVariables = new LsfInterpolate2PolyEncVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, lp, 0, 11);
			index = 0;
		}
	}

	public static final class SimpleLpcAnalysisVariables
	{
		short[] A = new short[11];
		short[] windowedData = new short[240];
		short[] rc = new short[10];
		int[] R = new int[11];

		int j = 0;

		short[] lpcBuffer;

		AutoCorrelationVariables autoCorrelationVariables = new AutoCorrelationVariables();
		LevinsonDurbinVariables levinsonDurbinVariables = new LevinsonDurbinVariables();
		Poly2LsfVariables poly2LsfVariables = new Poly2LsfVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, A, 0, 11);
			System.arraycopy(ILBC.emptyArray, 0, windowedData, 0, 240);
			System.arraycopy(ILBC.emptyArray, 0, rc, 0, 10);
			System.arraycopy(ILBC.emptyIntArray, 0, R, 0, 11);
		}
	}

	public static final class SimpleLsfQVariables
	{
		SplitVqVariables splitVqVariables = new SplitVqVariables();
	}

	public static final class StateConstructVariables
	{
		short[] numerator = new short[11];
		short[] sampleValVec = new short[126];
		short[] sampleMaVec = new short[126];
		short[] sampleVal = sampleValVec;
		short[] sampleMa = sampleMaVec;
		short[] sampleAr = sampleValVec;

		short[] idxVec;

		int coef, bitShift, currIndex, currIndex2;
		int k;
		int max;

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, numerator, 0, 11);
			System.arraycopy(ILBC.emptyArray, 0, sampleValVec, 0, 126);
			System.arraycopy(ILBC.emptyArray, 0, sampleMaVec, 0, 126);
		}
	}

	public static final class StateSearchVariables
	{
		short[] numerator = new short[11];
		short[] residualLongVec = new short[126];
		short[] sampleMa = new short[116];
		short[] residualLong = residualLongVec;
		short[] sampleAr = residualLongVec;

		int nBits, n, currIndex, temp;
		short max, tempS, tempS2;

		AbsQuantVariables absQuantVariables = new AbsQuantVariables();

		public void reset()
		{
			System.arraycopy(ILBC.emptyArray, 0, numerator, 0, 11);
			System.arraycopy(ILBC.emptyArray, 0, residualLongVec, 0, 126);
			System.arraycopy(ILBC.emptyArray, 0, sampleMa, 0, 116);
		}
	}

	public static final class UnpackBitsVariables
	{
		short tempIndex1 = 0;
		short tempIndex2 = 0;
		short tempS;
		int i;
		int k;

		short[] lsf;
		short[] cbIndex;
		short[] gainIndex;
		short[] idxVec;

		public void reset()
		{
			tempIndex1 = 0;
			tempIndex2 = 0;
		}
	}

	public static final class UpdateDecIndexVariables
	{
		short[] index;
		int k;
	}

	public static final class EncoderState
	{
		short[] anaMem = new short[10];
		short[] lsfOld = new short[10];
		short[] lsfDeqOld = new short[10];
		short[] lpcBuffer = new short[300];
		short[] hpiMemX = new short[2];
		short[] hpiMemY = new short[4];

		public void reset()
		{
			System.arraycopy(ILBC.LSF_MEAN, 0, lsfOld, 0, ILBC.LSF_MEAN.length);
			System.arraycopy(ILBC.LSF_MEAN, 0, lsfDeqOld, 0, ILBC.LSF_MEAN.length);
			System.arraycopy(ILBC.emptyArray, 0, anaMem, 0, anaMem.length);

			// System.arraycopy(CodingFunctions.emptyArray, 0, lsfOld, 0,
			// lsfOld.length); //----------------
			// System.arraycopy(CodingFunctions.emptyArray, 0, lsfDeqOld, 0,
			// lsfDeqOld.length);//------------------

			System.arraycopy(ILBC.emptyArray, 0, lpcBuffer, 0, lpcBuffer.length);
			System.arraycopy(ILBC.emptyArray, 0, hpiMemX, 0, hpiMemX.length);
			System.arraycopy(ILBC.emptyArray, 0, hpiMemY, 0, hpiMemY.length);
		}
	}

	public static final class DecoderState
	{
		short[] synthMem = new short[10];
		short[] lsfDeqOld = new short[10];
		short[] prevLpc = new short[11];
		short[] prevResidual = new short[240];
		short[] oldSyntDenum = new short[66];
		short[] enhancementBuffer = new short[643];
		short[] enhancementPeriod = new short[8];
		short[] hpiMemX = new short[2];
		short[] hpiMemY = new short[4];

		int lastLag;
		int consPliCount;
		int prevEnchPl;
		int useEnhancer;
		short perSquare, prevScale, prevPli, prevLag, seed;

		public void reset()
		{
			for (int i = 0; i < enhancementPeriod.length; i++)
			{
				enhancementPeriod[i] = 160;
			}

			for (int i = 0; i < 6; i++)
			{
				oldSyntDenum[i * 11] = 4096;
			}
			seed = 777;
			prevLag = 120;
			lastLag = 20;
			useEnhancer = 0;
			consPliCount = 0;
			prevEnchPl = 0;
			perSquare = 0;
			prevScale = 0;
			prevPli = 0;
			prevLpc[0] = 4096;

			System.arraycopy(ILBC.emptyArray, 0, synthMem, 0, synthMem.length);
			System.arraycopy(ILBC.LSF_MEAN, 0, lsfDeqOld, 0, lsfDeqOld.length);
			System.arraycopy(ILBC.emptyArray, 0, prevLpc, 1, prevLpc.length - 1);
			System.arraycopy(ILBC.emptyArray, 0, prevResidual, 0, prevResidual.length);
			System.arraycopy(ILBC.emptyArray, 0, oldSyntDenum, 0, oldSyntDenum.length);
			System.arraycopy(ILBC.emptyArray, 0, enhancementBuffer, 0, enhancementBuffer.length);
			System.arraycopy(ILBC.emptyArray, 0, enhancementPeriod, 0, enhancementPeriod.length);
			System.arraycopy(ILBC.emptyArray, 0, hpiMemX, 0, hpiMemX.length);
			System.arraycopy(ILBC.emptyArray, 0, hpiMemY, 0, hpiMemY.length);
		}
	}

	public enum Mode
	{
		MODE_20(20, 160, 4, 2, 38, 19, 1, 57), MODE_30(30, 240, 6, 4, 50, 25, 2, 58);

		public int value;
		public short size;
		public short subframes;
		public short nasub;
		public short bytes;
		public short words;
		public short lpc_n;
		public short state_short_len;

		private Mode(int value, int size, int subframes, int nasub, int bytes, int words, int lpc_n, int state_short_len)
		{
			this.value = value;
			this.size = (short) size;
			this.subframes = (short) subframes;
			this.nasub = (short) nasub;
			this.bytes = (short) bytes;
			this.words = (short) words;
			this.lpc_n = (short) lpc_n;
			this.state_short_len = (short) state_short_len;
		}
	}


	public static class BasicFunctions
	{
		public static short abs(short var1)
		{
			if (var1 == Short.MIN_VALUE)
				return Short.MAX_VALUE;
			else if (var1 < 0)
				return (short) -var1;

			return (short) var1;
		}

		public static short getSize(int var1) throws ArithmeticException
		{
			short count;
			if ((0xFFFF0000 & var1) != 0)
				count = 16;
			else count = 0;

			if ((0x0000FF00 & (var1 >> count)) != 0)
				count += 8;
			if ((0x000000F0 & (var1 >> count)) != 0)
				count += 4;
			if ((0x0000000C & (var1 >> count)) != 0)
				count += 2;
			if ((0x00000002 & (var1 >> count)) != 0)
				count += 1;
			if ((0x00000001 & (var1 >> count)) != 0)
				count += 1;

			return count;
		}

		public static short norm(int var1) throws ArithmeticException
		{
			short count = 0;
			int currValue = var1;
			if (currValue <= 0)
				currValue ^= 0xFFFFFFFF;

			if ((0xFFFF8000 & currValue) == 0)
				count = 16;
			else count = 0;

			if ((0xFF800000 & (currValue << count)) == 0)
				count += 8;
			if ((0xF8000000 & (currValue << count)) == 0)
				count += 4;
			if ((0xE0000000 & (currValue << count)) == 0)
				count += 2;
			if ((0xC0000000 & (currValue << count)) == 0)
				count += 1;

			return count;
		}

		public static int div(int num, short hi, short low)
		{
			short temp1, temp2, numHi, numLow;
			int temp, tempL, tempShift, approx;

			if (hi != 0)
				approx = 0x1FFFFFFF / hi;
			else approx = Integer.MAX_VALUE;

			tempL = hi * approx;
			tempL = tempL << 1;
			temp = tempL;
			tempL = low * approx;
			tempL = tempL >> 15;
			tempL = tempL << 1;
			temp += tempL;
			temp = Integer.MAX_VALUE - temp;
			temp1 = (short) (temp >> 16);
			tempShift = temp1 << 16;
			temp2 = (short) ((temp - tempShift) >> 1);

			tempL = (temp1 * approx);
			temp = tempL;
			tempL = temp2 * approx;
			tempL = tempL >> 15;
			temp += tempL;
			temp = temp << 1;
			temp1 = (short) (temp >> 16);
			tempShift = temp1 << 16;
			temp2 = (short) ((temp - tempShift) >> 1);

			numHi = (short) (num >> 16);
			tempShift = numHi << 16;
			numLow = (short) ((num - tempShift) >> 1);

			tempL = numHi * temp1;
			temp = tempL;
			tempL = numHi * temp2;
			tempL = tempL >> 15;
			temp += tempL;
			tempL = numLow * temp1;
			tempL = (tempL) >> 15;
			temp += tempL;
			temp = temp << 3;
			return temp;
		}

		// MaxAbsValue
		public static short getMaxAbsValue(short[] input, int inputIndex, int length)
		{
			short currValue = 0;
			int i = inputIndex;
			int total = inputIndex + length;
			for (; i < total; i++)
				if (input[i] > 0 && input[i] > currValue)
					currValue = input[i];
				else if (input[i] < 0 && (0 - input[i]) > currValue)
					currValue = (short) (0 - input[i]);

			return currValue;
		}

		public static int getMaxAbsValue(int[] input, int inputIndex, int length)
		{
			int currValue = 0;
			int i = inputIndex;
			int total = inputIndex + length;
			for (; i < total; i++)
				if (input[i] > 0 && input[i] > currValue)
					currValue = input[i];
				else if (input[i] < 0 && (0 - input[i]) > currValue)
					currValue = (short) (0 - input[i]);

			return currValue;
		}

		public static int getMaxIndex(int[] input, int inputIndex, int length)
		{
			int currValue = input[inputIndex];
			int i = inputIndex + 1;
			int total = inputIndex + length;
			int currIndex = inputIndex;
			for (; i < total; i++)
				if (input[i] > currValue)
				{
					currValue = input[i];
					currIndex = i;
				}

			return currIndex;
		}

		public static int getMinIndex(int[] input, int inputIndex, int length)
		{
			int currValue = input[inputIndex];
			int i = inputIndex + 1;
			int total = inputIndex + length;
			int currIndex = inputIndex;
			for (; i < total; i++)
				if (input[i] < currValue)
				{
					currValue = input[i];
					currIndex = i;
				}

			return currIndex;
		}

		// DotProductWithScale
		public static int scaleRight(short[] input1, int input1Index, short[] input2, int input2Index, int length, int rightShifts)
		{
			int sum = 0;
			for (int n = 0; n < length; n++)
				sum += ((input1[input1Index++] * input2[input2Index++]) >> rightShifts);

			return sum;
		}

		public static int scaleLeft(short[] input1, int input1Index, short[] input2, int input2Index, int length, int leftShifts)
		{
			int sum = 0;
			for (int n = 0; n < length; n++)
				sum += ((input1[input1Index++] * input2[input2Index++]) << leftShifts);

			return sum;
		}

		// ScaleAndAddVectors
		public static void scaleAndAddVectors(short[] input1, int input1Index, short gain1, int rightShifts1, short[] input2, int input2Index, short gain2, int rightShifts2, short[] output, int outputIndex, int length)
		{
			int i;
			for (i = 0; i < length; i++)
				output[outputIndex++] = (short) (((gain1 * input1[input1Index++]) >> rightShifts1) + ((gain2 * input2[input2Index++]) >> rightShifts2));
		}

		// ScaleVectorWithSat
		public static void scaleVector(short[] input, int inputIndex, short[] output, int outputIndex, short gain, int length, int rightShifts)
		{
			int i;
			int temp;

			for (i = 0; i < length; i++)
			{
				temp = (input[inputIndex++] * gain) >> rightShifts;
				if (temp > Short.MAX_VALUE)
					output[outputIndex++] = Short.MAX_VALUE;
				else if (temp < Short.MIN_VALUE)
					output[outputIndex++] = Short.MIN_VALUE;
				else output[outputIndex++] = (short) temp;
			}
		}

		// ElementwiseVectorMult
		public static void multWithRightShift(short[] output, int outputIndex, short[] input1, int input1Index, short[] input2, int input2Index, int length, int rightShifts)
		{	
			for (int n = 0; n < length; n++)
				output[outputIndex++] = (short) ((input1[input1Index++] * input2[input2Index++]) >> rightShifts);
		}

		public static void multWithLeftShift(short[] output, int outputIndex, short[] input1, int input1Index, short[] input2, int input2Index, int length, int leftShifts)
		{
			for (int n = 0; n < length; n++)
				output[outputIndex++] = (short) ((input1[input1Index++] * input2[input2Index++]) << leftShifts);
		}

		// ReverseOrderMultArrayElements
		public static void reverseMultiplyRight(short[] out, int outIndex, short[] in, int inIndex, short[] win, int winIndex, int length, int rightShifts)
		{
			for (int n = 0; n < length; n++)
				out[outIndex++] = (short) ((in[inIndex++] * win[winIndex--]) >> rightShifts);
		}

		public static void reverseMultiplyLeft(short[] out, int outIndex, short[] in, int inIndex, short[] win, int winIndex, int length, int leftShifts)
		{
			for (int n = 0; n < length; n++)
				out[outIndex++] = (short) ((in[inIndex++] * win[winIndex--]) << leftShifts);
		}

		// VectorBitShiftW32
		public static void bitShiftRight(int[] output, int outputIndex, int[] input, int inputIndex, int length, int rightShifts)
		{
			for (int n = 0; n < length; n++)
				output[outputIndex++] = input[inputIndex++] >> rightShifts;
		}

		public static void bitShiftLeft(int[] output, int outputIndex, int[] input, int inputIndex, int length, int leftShifts)
		{
			for (int n = 0; n < length; n++)
				output[outputIndex++] = input[inputIndex++] << leftShifts;
		}

		// AddVectorsAndShift
		public static void addWithRightShift(short[] out, int outIndex, short[] in1, int in1Index, short[] in2, int in2Index, int length, int rightShifts)
		{
			for (int i = length; i > 0; i--)
				out[outIndex++] = (short) ((in1[in1Index++] + in2[in2Index++]) >> rightShifts);
		}

		public static void addWithLeftShift(short[] out, int outIndex, short[] in1, int in1Index, short[] in2, int in2Index, int length, int leftShifts)
		{
			for (int i = length; i > 0; i--)
				out[outIndex++] = (short) ((in1[in1Index++] + in2[in2Index++]) << leftShifts);
		}

		// AddAffineVectorToVector
		public static void addAffineVectorToVector(short[] out, int outIndex, short[] in, int inIndex, short gain, int addConstant, short rightShifts, int length)
		{
			for (int n = 0; n < length; n++)
				out[outIndex++] += (short) ((in[inIndex++] * gain + addConstant) >> rightShifts);
		}

		// BwExpand
		public static void expand(short[] output, int outputIndex, short[] input, int inputIndex, short[] coeficient, int length)
		{
			output[outputIndex++] = input[inputIndex++];
			for (int i = 1; i < length; i++)
			{
				/*
				 * out[i] = coef[i] * in[i] with rounding. in[] and out[] are in Q12
				 * and coef[] is in Q15
				 */
				output[outputIndex++] = (short) ((coeficient[i] * input[inputIndex++] + 16384) >> 15);
			}
		}

		// WebRtcSpl_MemCpyReversedOrder
		public static void reverseCopy(short[] dest, int destIndex, short[] source, int sourceIndex, int length)
		{
			while (length > 0)
			{
				dest[destIndex--] = source[sourceIndex++];
				length--;
			}
		}

		// FilterMAFastQ12
		public static void filterMA(short[] input, int inputIndex, short[] output, int outputIndex, short[] B, int bIndex, int bLength, int length)
		{
			int o;
			int i, j;
			int temp1, temp2;
			for (i = 0; i < length; i++)
			{
				temp2 = inputIndex + i;
				temp1 = bIndex;
				o = 0;

				for (j = 0; j < bLength; j++)
					o += B[temp1++] * input[temp2--];

				// If output is higher than 32768, saturate it. Same with negative
				// side
				// 2^27 = 134217728, which corresponds to 32768 in Q12

				// Saturate the output
				if (o > 134215679)
					o = 134215679;
				else if (o < -134217728)
					o = -134217728;

				output[outputIndex++] = (short) ((o + 2048) >> 12);
			}
		}

		// FilterARFastQ12
		public static void filterAR(short[] input, int inputIndex, short[] output, int outputIndex, short[] coefs, int coefsIndex, int coefsLength, int length)
		{
			int i, j;
			int temp2, temp3;
			int result = 0, sum = 0;
			for (i = 0; i < length; i++)
			{
				result = 0;
				sum = 0;
				temp2 = outputIndex + i - coefsLength + 1;
				temp3 = coefsIndex + coefsLength - 1;
				for (j = coefsLength - 1; j > 0; j--)
					sum += coefs[temp3--] * output[temp2++];

				result = coefs[coefsIndex] * input[inputIndex++];
				result -= sum;

				// Saturate and store the output.
				if (result > 134215679)
					result = 134215679;
				else if (result < -134217728)
					result = -134217728;

				output[temp2] = (short) ((result + 2048) >> 12);
			}
		}

		public static int downsampleFast(short[] dataIn, int dataInIndex, int dataInLength, short[] dataOut, int dataOutIndex, int dataOutLength, short[] coef, int coefLength, int factor, int delay)
		{
			int i = 0;
			int j = 0;
			int out = 0;
			int endpos = delay + factor * (dataOutLength - 1) + 1;

			if (dataOutLength <= 0 || coefLength <= 0 || dataInLength < endpos)
				return -1;

			for (i = delay; i < endpos; i += factor)
			{
				out = 2048;

				for (j = 0; j < coefLength; j++)
					out += coef[j] * dataIn[dataInIndex + i - j];

				out >>= 12;
				if (out > 32767)
					out = 32767;
				else if (out < -32768)
					out = -32768;

				dataOut[dataOutIndex++] = (short) out;
			}

			return 0;
		}

		public static int sqrtFloor(int value)
		{
			int try1, root = 0;
			int temp;
			for (int i = 15; i >= 0; i--)
			{
				try1 = root + (1 << i);
				temp = (try1 << i);
				if (value >= temp)
				{
					value -= temp;
					root = root | (2 << i);
				}
			}

			return (root >> 1);
		}
	}

	public static class CodingFunctions
	{
		public static void hpInput(EncoderState encoderState, short[] data, int startIndex, int length, HpInputVariables variables)
		{
			variables.ba = ILBC.HP_IN_COEFICIENTS;
			variables.y = encoderState.hpiMemY;
			variables.x = encoderState.hpiMemX;

			variables.currIndex = startIndex + length;

			for (variables.i = startIndex; variables.i < variables.currIndex; variables.i++)
			{
				variables.current = variables.y[1] * variables.ba[3];
				variables.current += variables.y[3] * variables.ba[4];
				variables.current = variables.current >> 15;
				variables.current += variables.y[0] * variables.ba[3];
				variables.current += variables.y[2] * variables.ba[4];
				variables.current = variables.current << 1;

				variables.current += data[variables.i] * variables.ba[0];
				variables.current += variables.x[0] * variables.ba[1];
				variables.current += variables.x[1] * variables.ba[2];

				variables.x[1] = variables.x[0];
				variables.x[0] = data[variables.i];

				if (variables.current > 268431359)
					data[variables.i] = Short.MAX_VALUE;
				else if (variables.current < -268439552)
					data[variables.i] = Short.MIN_VALUE;
				else data[variables.i] = (short) ((variables.current + 4096) >> 13);

				variables.y[2] = variables.y[0];
				variables.y[3] = variables.y[1];

				if (variables.current > 268435455)
					variables.current = Integer.MAX_VALUE;
				else if (variables.current < -268435456)
					variables.current = Integer.MIN_VALUE;
				else variables.current = variables.current << 3;

				variables.y[0] = (short) (variables.current >> 16);
				variables.y[1] = (short) ((variables.current - (variables.y[0] << 16)) >> 1);

			}
		}

		public static void hpOutput(short[] signal, int signalIndex, short[] ba, short[] y, short[] x, short len, HpOutputVariables variables)
		{

			for (variables.i = 0; variables.i < len; variables.i++)
			{
				/*
				 * y[i] = b[0]*x[i] + b[1]*x[i-1] + b[2]*x[i-2] + (-a[1])*y[i-1] +
				 * (-a[2])*y[i-2];
				 */
				variables.temp = y[1] * ba[3]; /* (-a[1])*y[i-1] (low part) */
				variables.temp += y[3] * ba[4]; /* (-a[2])*y[i-2] (low part) */
				variables.temp = variables.temp >> 15;
				variables.temp += y[0] * ba[3]; /* (-a[1])*y[i-1] (high part) */
				variables.temp += y[2] * ba[4]; /* (-a[2])*y[i-2] (high part) */
				variables.temp = variables.temp << 1;

				variables.temp += signal[variables.i] * ba[0]; /* b[0]*x[0] */
				variables.temp += x[0] * ba[1]; /* b[1]*x[i-1] */
				variables.temp += x[1] * ba[2]; /* b[2]*x[i-2] */

				/* Update state (input part) */
				x[1] = x[0];
				x[0] = signal[variables.i];

				/* Rounding in Q(12-1), i.e. add 2^10 */
				variables.temp2 = variables.temp + 1024;

				/*
				 * Saturate (to 2^26) so that the HP filtered signal does not
				 * overflow
				 */
				if (variables.temp2 > 67108863)
					variables.temp2 = 67108863;
				else if (variables.temp2 < -67108864)
					variables.temp2 = -67108864;

				/* Convert back to Q0 and multiply with 2 */
				signal[signalIndex++] = (short) (variables.temp2 >> 11);

				/* Update state (filtered part) */
				y[2] = y[0];
				y[3] = y[1];

				/* upshift tmpW32 by 3 with saturation */
				if (variables.temp > 268435455)
					variables.temp = Integer.MAX_VALUE;
				else if (variables.temp < -268435456)
					variables.temp = Integer.MIN_VALUE;
				else variables.temp = variables.temp << 3;

				y[0] = (short) (variables.temp >> 16);
				variables.tempShift = y[0] << 16;
				y[1] = (short) ((variables.temp - variables.tempShift) >> 1);
			}
		}

		public static void lpcEncode(EncoderState encoderState, EncoderBits encoderBits, short[] synthDenum, int synthDenumIndex, short[] weightDenum, int weightDenumIndex, short[] data, int startIndex, LpcEncodeVariables variables,Mode mode)
		{
			variables.reset();

			simpleLpcAnalysis(encoderState, variables.lsf, 0, data, startIndex, variables.simpleLpcAnalysisVariables,mode);
			simpleLsfQ(encoderBits, variables.lsfDeq, 0, variables.lsf, 0, variables.simpleLsfQVariables);
			lsfCheck(variables.lsfDeq, 0, 10, variables.lsfCheckVariables);
			simpleInterpolateLsf(encoderState, synthDenum, synthDenumIndex, weightDenum, weightDenumIndex, variables.lsf, 0, variables.lsfDeq, 0, 10, variables.simpleInterpolateLsfVariables,mode);
		}

		public static short[] simpleLpcAnalysis(EncoderState encoderState, short[] lsf, int lsfIndex, short[] data, int startIndex, SimpleLpcAnalysisVariables variables,Mode mode)
		{
			variables.reset();
			variables.lpcBuffer = encoderState.lpcBuffer;

			System.arraycopy(data, startIndex, variables.lpcBuffer, 300 - mode.size, mode.size);

			BasicFunctions.multWithRightShift(variables.windowedData, 0, variables.lpcBuffer, 60, ILBC.LPC_ASYM_WIN, 0, 240, 15);
			autoCorrelation(variables.windowedData, 0, 240, 10, variables.R, 0, variables.autoCorrelationVariables);
			windowMultiply(variables.R, 0, variables.R, 0, ILBC.LPC_LAG_WIN, 11);
			if (!levinsonDurbin(variables.R, 0, variables.A, 0, variables.rc, 0, 10, variables.levinsonDurbinVariables))
			{
				variables.A[0] = 4096;
				for (variables.j = 1; variables.j < 11; variables.j++)
					variables.A[variables.j] = 0;
			}

			BasicFunctions.expand(variables.A, 0, variables.A, 0, ILBC.LPC_CHIRP_SYNT_DENUM, 11);
			poly2Lsf(lsf, lsfIndex, variables.A, 0, variables.poly2LsfVariables);

			System.arraycopy(variables.lpcBuffer, mode.size, variables.lpcBuffer, 0, 300 - mode.size);
			return variables.A;
		}

		public static void autoCorrelation(short[] input, int inputIndex, int inputLength, int order, int[] result, int resultIndex, AutoCorrelationVariables variables)
		{
			variables.max = 0;

			if (order < 0)
				order = inputLength;

			for (variables.i = 0; variables.i < inputLength; variables.i++)
			{
				variables.tempS = BasicFunctions.abs(input[inputIndex++]);
				if (variables.tempS > variables.max)
					variables.max = variables.tempS;
			}

			inputIndex -= inputLength;
			if (variables.max == 0)
				variables.scale = 0;
			else
			{
				variables.nBits = BasicFunctions.getSize(inputLength);
				variables.tempS = BasicFunctions.norm(variables.max * variables.max);

				if (variables.tempS > variables.nBits)
					variables.scale = 0;
				else variables.scale = (short) (variables.nBits - variables.tempS);
			}

			for (variables.i = 0; variables.i < order + 1; variables.i++)
			{
				result[resultIndex] = 0;
				variables.currIndex1 = inputIndex;
				variables.currIndex2 = inputIndex + variables.i;
				for (variables.j = inputLength - variables.i; variables.j > 0; variables.j--)
					result[resultIndex] += ((input[variables.currIndex1++] * input[variables.currIndex2++]) >> variables.scale);

				resultIndex++;
			}
		}

		public static void windowMultiply(int[] output, int outputIndex, int[] input, int inputIndex, int[] window, int length)
		{
			int i;
			short xHi, xLow, yHi, yLow;
			int nBits = BasicFunctions.norm(input[inputIndex]);
			BasicFunctions.bitShiftLeft(input, inputIndex, input, inputIndex, length, nBits);

			for (i = 0; i < length; i++)
			{
				xHi = (short) (input[inputIndex] >> 16);
				yHi = (short) (window[i] >> 16);

				xLow = (short) ((input[inputIndex++] - (xHi << 16)) >> 1);
				yLow = (short) ((window[i] - (yHi << 16)) >> 1);

				output[outputIndex] = (xHi * yHi) << 1;
				output[outputIndex] += (xHi * yLow) >> 14;
				output[outputIndex++] += (xLow * yHi) >> 14;
			}

			outputIndex -= length;
			BasicFunctions.bitShiftRight(output, outputIndex, output, outputIndex, length, nBits);
		}

		public static boolean levinsonDurbin(int[] R, int rIndex, short[] A, int aIndex, short[] K, int kIndex, int order, LevinsonDurbinVariables variables)
		{
			variables.reset();
			variables.nBits = BasicFunctions.norm(R[rIndex]);
			variables.currIndex = rIndex + order;

			for (variables.i = order; variables.i >= 0; variables.i--)
			{
				variables.temp = R[variables.currIndex--] << variables.nBits;
				variables.rHi[variables.i] = (short) (variables.temp >> 16);
				variables.rLow[variables.i] = (short) ((variables.temp - (variables.rHi[variables.i] << 16)) >> 1);
			}

			variables.temp2 = variables.rHi[1] << 16;
			variables.temp3 = variables.rLow[1] << 1;
			variables.temp2 += variables.temp3;

			if (variables.temp2 > 0)
				variables.temp3 = variables.temp2;
			else variables.temp3 = -variables.temp2;

			variables.temp = BasicFunctions.div(variables.temp3, variables.rHi[0], variables.rLow[0]);
			if (variables.temp2 > 0)
				variables.temp = -variables.temp;

			variables.xHi = (short) (variables.temp >> 16);
			variables.xLow = (short) ((variables.temp - (variables.xHi << 16)) >> 1);

			K[kIndex++] = variables.xHi;
			variables.temp = variables.temp >> 4;

			variables.aHi[1] = (short) (variables.temp >> 16);
			variables.aLow[1] = (short) ((variables.temp - (variables.aHi[1] << 16)) >> 1);

			variables.temp = (variables.xHi * variables.xLow) >> 14;
			variables.temp += variables.xHi * variables.xHi;
			variables.temp <<= 1;

			if (variables.temp < 0)
				variables.temp = 0 - variables.temp;

			variables.temp = Integer.MAX_VALUE - variables.temp;

			variables.tempS = (short) (variables.temp >> 16);
			variables.tempS2 = (short) ((variables.temp - (variables.tempS << 16)) >> 1);

			variables.temp = variables.rHi[0] * variables.tempS;
			variables.temp += (variables.rHi[0] * variables.tempS2) >> 15;
			variables.temp += (variables.rLow[0] * variables.tempS) >> 15;
			variables.temp <<= 1;

			variables.alphaExp = BasicFunctions.norm(variables.temp);
			variables.temp = variables.temp << variables.alphaExp;
			variables.yHi = (short) (variables.temp >> 16);
			variables.yLow = (short) ((variables.temp - (variables.yHi << 16)) >> 1);

			for (variables.i = 2; variables.i <= order; variables.i++)
			{
				variables.temp = 0;
				variables.currIndex = variables.i - 1;
				for (variables.j = 1; variables.j < variables.i; variables.j++)
				{
					variables.temp2 = (variables.rHi[variables.j] * variables.aLow[variables.currIndex]) >> 15;
					variables.temp2 += (variables.rLow[variables.j] * variables.aHi[variables.currIndex]) >> 15;
					variables.temp += variables.temp2 << 1;
					variables.temp += (variables.rHi[variables.j] * variables.aHi[variables.currIndex]) << 1;
					variables.currIndex--;
				}

				variables.temp = variables.temp << 4;
				variables.temp += variables.rHi[variables.i] << 16;
				variables.temp += variables.rLow[variables.i] << 1;

				variables.temp2 = Math.abs(variables.temp);
				variables.temp3 = BasicFunctions.div(variables.temp2, variables.yHi, variables.yLow);

				if (variables.temp > 0)
					variables.temp3 = -variables.temp3;

				variables.nBits = BasicFunctions.norm(variables.temp3);

				if (variables.alphaExp <= variables.nBits || variables.temp3 == 0)
					variables.temp3 = variables.temp3 << variables.alphaExp;
				else
				{
					if (variables.temp3 > 0)
						variables.temp3 = Integer.MAX_VALUE;
					else variables.temp3 = Integer.MIN_VALUE;
				}

				variables.xHi = (short) (variables.temp3 >> 16);
				variables.xLow = (short) ((variables.temp3 - (variables.xHi << 16)) >> 1);

				K[kIndex++] = variables.xHi;

				if (BasicFunctions.abs(variables.xHi) > 32750)
					return false; // Unstable filter

				variables.currIndex = variables.i - 1;
				variables.currIndex = variables.i - 1;
				for (variables.j = 1; variables.j < variables.i; variables.j++)
				{
					variables.temp = variables.aHi[variables.j] << 16;
					variables.temp += variables.aLow[variables.j] << 1;
					variables.temp2 = (variables.xLow * variables.aHi[variables.currIndex]) >> 15;
					variables.temp2 += (variables.xHi * variables.aLow[variables.currIndex]) >> 15;
					variables.temp2 += variables.xHi * variables.aHi[variables.currIndex];
					variables.temp += variables.temp2 << 1;

					variables.aUpdHi[variables.j] = (short) (variables.temp >> 16);
					variables.aUpdLow[variables.j] = (short) ((variables.temp - (variables.aUpdHi[variables.j] << 16)) >> 1);
					variables.currIndex--;
				}

				variables.temp3 = variables.temp3 >> 4;

				variables.aUpdHi[variables.i] = (short) (variables.temp3 >> 16);
				variables.aUpdLow[variables.i] = (short) ((variables.temp3 - (variables.aUpdHi[variables.i] << 16)) >> 1);

				variables.temp = (((variables.xHi * variables.xLow) >> 14) + variables.xHi * variables.xHi) << 1;
				if (variables.temp < 0)
					variables.temp = 0 - variables.temp;

				variables.temp = Integer.MAX_VALUE - variables.temp;
				variables.tempS = (short) (variables.temp >> 16);
				variables.tempS2 = (short) ((variables.temp - (variables.tempS << 16)) >> 1);

				variables.temp2 = (variables.yHi * variables.tempS2) >> 15;
				variables.temp2 += (variables.yLow * variables.tempS) >> 15;
				variables.temp2 += variables.yHi * variables.tempS;
				variables.temp = variables.temp2 << 1;

				variables.nBits = BasicFunctions.norm(variables.temp);
				variables.temp = variables.temp << variables.nBits;

				variables.yHi = (short) (variables.temp >> 16);
				variables.yLow = (short) ((variables.temp - (variables.yHi << 16)) >> 1);
				variables.alphaExp = (short) (variables.alphaExp + variables.nBits);

				for (variables.j = 1; variables.j <= variables.i; variables.j++)
				{
					variables.aHi[variables.j] = variables.aUpdHi[variables.j];
					variables.aLow[variables.j] = variables.aUpdLow[variables.j];
				}
			}

			A[aIndex++] = 4096;
			for (variables.i = 1; variables.i <= order; variables.i++)
			{
				variables.temp = variables.aHi[variables.i] << 16;
				variables.temp += variables.aLow[variables.i] << 1;
				variables.temp <<= 1;
				variables.temp += 32768;
				A[aIndex++] = (short) (variables.temp >> 16);
			}

			return true;
		}

		public static void simpleInterpolateLsf(EncoderState encoderState, short[] synthdenum, int synthDenumIndex, short[] weightDenum, int weightDenumIndex, short[] lsf, int lsfIndex, short[] lsfDeq, int lsfDeqIndex, int length, SimpleInterpolateLsfVariables variables,Mode mode)
		{
			variables.reset();
			variables.lsfOld = encoderState.lsfOld;
			variables.lsfDeqOld = encoderState.lsfDeqOld;

			variables.step = length + 1;

			for (variables.i = 0; variables.i < mode.subframes; variables.i++)
			{
				lsfInterpolate2PolyEnc(variables.lp, (short) 0, variables.lsfDeqOld, 0, lsfDeq, lsfDeqIndex, ILBC.LSF_WEIGHT_20MS[variables.i], length, variables.lsfInterpolate2PolyEncVariables);
				System.arraycopy(variables.lp, 0, synthdenum, synthDenumIndex + variables.index, variables.step);

				lsfInterpolate2PolyEnc(variables.lp, (short) 0, variables.lsfOld, 0, lsf, lsfIndex, ILBC.LSF_WEIGHT_20MS[variables.i], length, variables.lsfInterpolate2PolyEncVariables);
				BasicFunctions.expand(weightDenum, weightDenumIndex + variables.index, variables.lp, 0, ILBC.LPC_CHIRP_WEIGHT_DENUM, variables.step);

				variables.index += variables.step;
			}

			System.arraycopy(lsf, lsfIndex, variables.lsfOld, 0, length);
			System.arraycopy(lsfDeq, lsfDeqIndex, variables.lsfDeqOld, 0, length);
		}

		public static void lsfInterpolate2PolyEnc(short[] a, short aIndex, short[] lsf1, int lsf1Index, short[] lsf2, int lsf2Index, short coef, int length, LsfInterpolate2PolyEncVariables variables)
		{
			variables.reset();
			interpolate(variables.lsfTemp, 0, lsf1, lsf1Index, lsf2, lsf2Index, coef, length, variables.interpolateVariables);
			lsf2Poly(a, aIndex, variables.lsfTemp, 0, variables.lsf2PolyVariables);
		}

		public static void interpolate(short[] out, int outIndex, short[] in1, int in1Index, short[] in2, int in2Index, short coef, int length, InterpolateVariables variables)
		{
			variables.tempS = (short) (16384 - coef);
			for (variables.k = 0; variables.k < length; variables.k++)
				out[outIndex++] = (short) ((coef * in1[in1Index++] + variables.tempS * in2[in2Index++] + 8192) >> 14);
		}

		public static void lsf2Poly(short[] a, int aIndex, short[] lsf, int lsfIndex, Lsf2PolyVariables variables)
		{
			variables.reset();
			lsf2Lsp(lsf, lsfIndex, variables.lsp, 0, 10, variables.lsf2LspVariables);

			getLspPoly(variables.lsp, 0, variables.f1, 0, variables.getLspPolyVariables);
			getLspPoly(variables.lsp, 1, variables.f2, 0, variables.getLspPolyVariables);

			for (variables.k = 5; variables.k > 0; variables.k--)
			{
				variables.f1[variables.k] += variables.f1[variables.k - 1];
				variables.f2[variables.k] -= variables.f2[variables.k - 1];
			}

			a[aIndex] = 4096;

			int aStartIndex = aIndex + 1;
			int aEndIndex = aIndex + 10;

			int currIndex = 1;
			for (variables.k = 5; variables.k > 0; variables.k--)
			{
				a[aStartIndex++] = (short) (((variables.f1[currIndex] + variables.f2[currIndex]) + 4096) >> 13);
				a[aEndIndex--] = (short) (((variables.f1[currIndex] - variables.f2[currIndex]) + 4096) >> 13);
				currIndex++;
			}
		}

		public static void lsf2Lsp(short[] lsf, int lsfIndex, short[] lsp, int lspIndex, int count, Lsf2LspVariables variables)
		{
			for (variables.j = 0; variables.j < count; variables.j++)
			{
				variables.tempS = (short) ((lsf[lsfIndex++] * 20861) >> 15);
				variables.tempS2 = (short) (variables.tempS >> 8);
				variables.tempS = (short) (variables.tempS & 0x00ff);

				if (variables.tempS2 > 63 || variables.tempS2 < 0)
					variables.tempS2 = 63;

				lsp[lspIndex++] = (short) (((ILBC.COS_DERIVATIVE[variables.tempS2] * variables.tempS) >> 12) + ILBC.COS[variables.tempS2]);
			}
		}

		public static void getLspPoly(short[] lsp, int lspIndex, int[] f, int fIndex, GetLspPolyVariables variables)
		{
			f[fIndex++] = 16777216;
			f[fIndex++] = lsp[lspIndex] * (-1024);
			lspIndex += 2;

			for (variables.k = 2; variables.k <= 5; variables.k++)
			{
				f[fIndex] = f[fIndex - 2];

				for (variables.j = variables.k; variables.j > 1; variables.j--)
				{
					variables.xHi = (short) (f[fIndex - 1] >> 16);
					variables.xLow = (short) ((f[fIndex - 1] - (variables.xHi << 16)) >> 1);

					f[fIndex] += f[fIndex - 2];
					f[fIndex--] -= ((variables.xHi * lsp[lspIndex]) << 2) + (((variables.xLow * lsp[lspIndex]) >> 15) << 2);
				}

				f[fIndex] -= lsp[lspIndex] << 10;

				fIndex += variables.k;
				lspIndex += 2;
			}
		}

		public static void poly2Lsf(short[] lsf, int lsfIndex, short[] A, int aIndex, Poly2LsfVariables variables)
		{
			variables.reset();

			poly2Lsp(A, aIndex, variables.lsp, 0, variables.poly2LspVariables);
			lspToLsf(variables.lsp, 0, lsf, lsfIndex, 10, variables.lspToLsfVariables);
		}

		public static void poly2Lsp(short[] A, int aIndex, short[] lsp, int lspIndex, Poly2LspVariables variables)
		{
			variables.reset();
			variables.aLowIndex = aIndex + 1;
			variables.aHighIndex = aIndex + 10;

			variables.f1[0] = 1024;
			variables.f2[0] = 1024;

			for (variables.i = 0; variables.i < 5; variables.i++)
			{
				variables.f1[variables.i + 1] = (short) (((A[variables.aLowIndex] + A[variables.aHighIndex]) >> 2) - variables.f1[variables.i]);
				variables.f2[variables.i + 1] = (short) (((A[variables.aLowIndex] - A[variables.aHighIndex]) >> 2) + variables.f2[variables.i]);

				variables.aLowIndex++;
				variables.aHighIndex--;
			}

			variables.current = variables.f1;
			variables.currIndex = lspIndex;
			variables.foundFreqs = 0;

			variables.xLow = ILBC.COS_GRID[0];
			variables.yLow = chebushev(variables.xLow, variables.current, 0, variables.chebushevVariables);

			for (variables.j = 1; variables.j < ILBC.COS_GRID.length && variables.foundFreqs < 10; variables.j++)
			{
				variables.xHi = variables.xLow;
				variables.yHi = variables.yLow;
				variables.xLow = ILBC.COS_GRID[variables.j];
				variables.yLow = chebushev(variables.xLow, variables.current, 0, variables.chebushevVariables);

				if (variables.yLow * variables.yHi <= 0)
				{
					for (variables.i = 0; variables.i < 4; variables.i++)
					{
						variables.xMid = (short) ((variables.xLow >> 1) + (variables.xHi >> 1));
						variables.yMid = chebushev(variables.xMid, variables.current, 0, variables.chebushevVariables);

						if (variables.yLow * variables.yMid <= 0)
						{
							variables.yHi = variables.yMid;
							variables.xHi = variables.xMid;
						}
						else
						{
							variables.yLow = variables.yMid;
							variables.xLow = variables.xMid;
						}
					}

					variables.x = (short) (variables.xHi - variables.xLow);
					variables.y = (short) (variables.yHi - variables.yLow);

					if (variables.y == 0)
						lsp[variables.currIndex++] = variables.xLow;
					else
					{
						variables.temp = variables.y;
						variables.y = BasicFunctions.abs(variables.y);
						variables.nBits = (short) (BasicFunctions.norm(variables.y) - 16);
						variables.y = (short) (variables.y << variables.nBits);

						if (variables.y != 0)
							variables.y = (short) (536838144 / variables.y);
						else variables.y = (short) Integer.MAX_VALUE;

						variables.y = (short) (((variables.x * variables.y) >> (19 - variables.nBits)) & 0xFFFF);

						if (variables.temp < 0)
							variables.y = (short) -variables.y;

						lsp[variables.currIndex++] = (short) (variables.xLow - (((variables.yLow * variables.y) >> 10) & 0xFFFF));
					}

					variables.foundFreqs++;
					if (variables.foundFreqs < 10)
					{
						variables.xLow = lsp[variables.currIndex - 1];
						if ((variables.foundFreqs % 2) == 0)
							variables.current = variables.f1;
						else variables.current = variables.f2;

						variables.yLow = chebushev(variables.xLow, variables.current, 0, variables.chebushevVariables);
					}
				}
			}

			if (variables.foundFreqs < 10)
				System.arraycopy(ILBC.LSP_MEAN, 0, lsp, lspIndex, 10);
		}

		public static short chebushev(short value, short[] coefs, int coefsIndex, ChebushevVariables variables)
		{
			variables.b2 = 0x1000000;
			variables.temp = value << 10;
			coefsIndex++;
			variables.temp += coefs[coefsIndex++] << 14;

			for (variables.n = 2; variables.n < 5; variables.n++)
			{
				variables.temp2 = variables.temp;

				variables.b1Hi = (short) (variables.temp >> 16);
				variables.b1Low = (short) ((variables.temp - (variables.b1Hi << 16)) >> 1);

				variables.temp = (((variables.b1Low * value) >> 15) + (variables.b1Hi * value)) << 2;
				variables.temp -= variables.b2;
				variables.temp += coefs[coefsIndex++] << 14;

				variables.b2 = variables.temp2;
			}

			variables.b1Hi = (short) (variables.temp >> 16);
			variables.b1Low = (short) ((variables.temp - (variables.b1Hi << 16)) >> 1);

			variables.temp = (((variables.b1Low * value) >> 15) << 1) + ((variables.b1Hi * value) << 1);
			variables.temp -= variables.b2;
			variables.temp += coefs[coefsIndex] << 13;

			if (variables.temp > 33553408)
				return Short.MAX_VALUE;
			else if (variables.temp < -33554432)
				return Short.MIN_VALUE;
			else return (short) (variables.temp >> 10);
		}

		public static void lspToLsf(short[] lsp, int lspIndex, short[] lsf, int lsfIndex, int coefsNumber, LspToLsfVariables variables)
		{
			variables.j = 63;
			variables.currLspIndex = lspIndex + coefsNumber - 1;
			variables.currLsfIndex = lsfIndex + coefsNumber - 1;

			for (variables.i = coefsNumber - 1; variables.i >= 0; variables.i--)
			{
				while (ILBC.COS[variables.j] < lsp[variables.currLspIndex] && variables.j > 0)
					variables.j--;

				variables.currValue = (variables.j << 9) + ((ILBC.ACOS_DERIVATIVE[variables.j] * (lsp[variables.currLspIndex--] - ILBC.COS[variables.j])) >> 11);
				lsf[variables.currLsfIndex--] = (short) ((variables.currValue * 25736) >> 15);
			}
		}

		public static void simpleLsfQ(EncoderBits encoderBits, short[] lsfdeq, int lsfdeqIndex, short[] lsfArray, int lsfArrrayIndex, SimpleLsfQVariables variables)
		{
			splitVq(lsfdeq, lsfdeqIndex, encoderBits.LSF, 0, lsfArray, lsfArrrayIndex, variables.splitVqVariables);
		}

		public static void splitVq(short[] qX, int qXIndex, short[] lsf, int lsfIndex, short[] X, int xIndex, SplitVqVariables variables)
		{
			vq3(qX, qXIndex, lsf, lsfIndex, ILBC.LSF_INDEX_CB[0], X, xIndex, ILBC.LSF_SIZE_CB[0], variables.vq3Variables);
			vq3(qX, qXIndex + ILBC.LSF_DIM_CB[0], lsf, lsfIndex + 1, ILBC.LSF_INDEX_CB[1], X, xIndex + ILBC.LSF_DIM_CB[0], ILBC.LSF_SIZE_CB[1], variables.vq3Variables);
			vq4(qX, qXIndex + ILBC.LSF_DIM_CB[0] + ILBC.LSF_DIM_CB[1], lsf, lsfIndex + 2, ILBC.LSF_INDEX_CB[2], X, xIndex + ILBC.LSF_DIM_CB[0] + ILBC.LSF_DIM_CB[1], ILBC.LSF_SIZE_CB[2], variables.vq4Variables);
		}

		public static void vq3(short[] qX, int qXIndex, short[] lsf, int lsfIndex, int cbIndex, short[] X, int xIndex, int cbSize, Vq3Variables variables)
		{
			variables.minValue = Integer.MAX_VALUE;
			variables.currIndex = 0;

			for (variables.j = 0; variables.j < cbSize; variables.j++)
			{
				variables.tempS = (short) (X[xIndex++] - ILBC.LSF_CB[cbIndex++]);
				variables.temp = variables.tempS * variables.tempS;
				variables.tempS = (short) (X[xIndex++] - ILBC.LSF_CB[cbIndex++]);
				variables.temp += variables.tempS * variables.tempS;
				variables.tempS = (short) (X[xIndex++] - ILBC.LSF_CB[cbIndex++]);
				variables.temp += variables.tempS * variables.tempS;

				xIndex -= 3;
				if (variables.temp < variables.minValue)
				{
					variables.minValue = variables.temp;
					variables.currIndex = variables.j;
				}
			}

			cbIndex -= 3 * cbSize;
			lsf[lsfIndex] = (short) variables.currIndex;
			variables.currIndex *= 3;
			variables.currIndex += cbIndex;
			for (variables.i = 0; variables.i < 3; variables.i++)
				qX[qXIndex++] = ILBC.LSF_CB[variables.currIndex++];
		}

		public static void vq4(short[] qX, int qXIndex, short[] lsf, int lsfIndex, int cbIndex, short[] X, int xIndex, int cbSize, Vq4Variables variables)
		{
			variables.minValue = Integer.MAX_VALUE;
			variables.currIndex = 0;

			for (variables.j = 0; variables.j < cbSize; variables.j++)
			{
				variables.tempS = (short) (X[xIndex++] - ILBC.LSF_CB[cbIndex++]);
				variables.temp = variables.tempS * variables.tempS;
				for (variables.i = 1; variables.i < 4; variables.i++)
				{
					variables.tempS = (short) (X[xIndex++] - ILBC.LSF_CB[cbIndex++]);
					variables.temp += variables.tempS * variables.tempS;
				}

				xIndex -= 4;
				if (variables.temp < variables.minValue)
				{
					variables.minValue = variables.temp;
					variables.currIndex = variables.j;
				}
			}

			cbIndex -= 4 * cbSize;
			lsf[lsfIndex] = (short) variables.currIndex;
			variables.currIndex *= 4;
			variables.currIndex += cbIndex;
			for (variables.i = 0; variables.i < 4; variables.i++)
				qX[qXIndex++] = ILBC.LSF_CB[variables.currIndex++];
		}

		public static void lsfCheck(short[] lsf, int lsfIndex, int lsfSize, LsfCheckVariables variables)
		{
			for (variables.n = 0; variables.n < 2; variables.n++)
			{
				for (variables.k = 0; variables.k < lsfSize - 1; variables.k++)
				{
					variables.currIndex1 = lsfIndex + variables.k;
					variables.currIndex2 = variables.currIndex1 + 1;

					if ((lsf[variables.currIndex2] - lsf[variables.currIndex1]) < ILBC.EPS)
					{
						if (lsf[variables.currIndex2] < lsf[variables.currIndex1])
						{
							lsf[variables.currIndex2] = (short) (lsf[variables.currIndex1] + ILBC.HALF_EPS);
							lsf[variables.currIndex1] = (short) (lsf[variables.currIndex2] - ILBC.HALF_EPS);
						}
						else
						{
							lsf[variables.currIndex1] -= ILBC.HALF_EPS;
							lsf[variables.currIndex2] += ILBC.HALF_EPS;
						}
					}

					if (lsf[variables.currIndex1] < ILBC.MIN_LSF)
						lsf[variables.currIndex1] = ILBC.MIN_LSF;

					if (lsf[variables.currIndex1] > ILBC.MAX_LSF)
						lsf[variables.currIndex1] = ILBC.MAX_LSF;
				}
			}
		}

		public static short gainDequant(short index, short maxIn, short stage)
		{
			if (maxIn < 0)
				maxIn = (short) (0 - maxIn);

			if (maxIn < 1638)
				maxIn = 1638;

			return (short) ((maxIn * ILBC.GAIN[stage][index] + 8192) >> 14);
		}

		public static short gainQuant(short gain, short maxIn, short stage, short[] index, int indexIndex, GainQuantVariables variables)
		{
			if (maxIn > 1638)
				variables.scale = maxIn;
			else variables.scale = 1638;

			variables.cb = ILBC.GAIN[stage];
			variables.temp = gain << 14;

			variables.cbIndex = (32 >> stage) >> 1;
			variables.nBits = (short) variables.cbIndex;
			for (variables.n = 4 - stage; variables.n > 0; variables.n--)
			{
				variables.nBits >>= 1;
				if (variables.temp > variables.scale * variables.cb[variables.cbIndex])
					variables.cbIndex += variables.nBits;
				else variables.cbIndex -= variables.nBits;
			}

			variables.temp2 = variables.scale * variables.cb[variables.cbIndex];
			if (variables.temp > variables.temp2)
			{
				if ((variables.scale * variables.cb[variables.cbIndex + 1] - variables.temp) < (variables.temp - variables.temp2))
					variables.cbIndex++;
			}
			else if ((variables.temp - variables.scale * variables.cb[variables.cbIndex - 1]) <= (variables.temp2 - variables.temp))
				variables.cbIndex--;

			variables.temp = (32 >> stage) - 1;
			if (variables.cbIndex > variables.temp)
				variables.cbIndex = variables.temp;

			index[indexIndex] = (short) variables.cbIndex;
			return (short) ((variables.scale * variables.cb[variables.cbIndex] + 8192) >> 14);
		}

		public static void cbMemEnergyAugmentation(short[] interpSamples, int interpSamplesIndex, short[] cbMem, int cbMemIndex, short scale, short baseSize, short[] energy, int energyIndex, short[] energyShifts, int energyShiftsIndex, CbMemEnergyAugmentationVariables variables)
		{
			energyIndex = energyIndex + baseSize - 20;
			energyShiftsIndex = energyShiftsIndex + baseSize - 20;
			cbMemIndex = cbMemIndex + 147;

			variables.en1 = BasicFunctions.scaleRight(cbMem, cbMemIndex - 19, cbMem, cbMemIndex - 19, 15, scale);
			variables.currIndex = cbMemIndex - 20;

			for (variables.n = 20; variables.n <= 39; variables.n++)
			{
				variables.en1 += (cbMem[variables.currIndex] * cbMem[variables.currIndex]) >> scale;
				variables.currIndex--;
				variables.currValue = variables.en1;

				/* interpolation */
				variables.currValue += BasicFunctions.scaleRight(interpSamples, interpSamplesIndex, interpSamples, interpSamplesIndex, 4, scale);
				interpSamplesIndex += 4;

				/* Compute energy for the remaining samples */
				variables.currValue += BasicFunctions.scaleRight(cbMem, cbMemIndex - variables.n, cbMem, cbMemIndex - variables.n, 40 - variables.n, scale);

				/* Normalize the energy and store the number of shifts */
				energyShifts[energyShiftsIndex] = BasicFunctions.norm(variables.currValue);
				variables.currValue = variables.currValue << energyShifts[energyShiftsIndex++];

				energy[energyIndex++] = (short) (variables.currValue >> 16);
			}
		}

		public static void cbMemEnergy(short range, short[] cb, int cbIndex, short[] filteredCB, int filteredCbIndex, short length, short targetLength, short[] energy, int energyIndex, short[] energyShifts, int energyShiftsIndex, short scale, short baseSize, CbMemEnergyVariables variables)
		{
			variables.currValue = BasicFunctions.scaleRight(cb, cbIndex + length - targetLength, cb, cbIndex + length - targetLength, targetLength, scale);
			energyShifts[energyShiftsIndex] = (short) BasicFunctions.norm(variables.currValue);
			energy[energyIndex] = (short) ((variables.currValue << energyShifts[energyShiftsIndex]) >> 16);
			energyCalc(variables.currValue, range, cb, cbIndex + length - targetLength - 1, cb, cbIndex + length - 1, energy, energyIndex, energyShifts, energyShiftsIndex, scale, (short) 0);

			variables.currValue = BasicFunctions.scaleRight(filteredCB, filteredCbIndex + length - targetLength, filteredCB, filteredCbIndex + length - targetLength, targetLength, scale);
			energyShifts[baseSize + energyShiftsIndex] = BasicFunctions.norm(variables.currValue);
			energy[baseSize + energyIndex] = (short) ((variables.currValue << energyShifts[baseSize + energyShiftsIndex]) >> 16);
			energyCalc(variables.currValue, range, filteredCB, filteredCbIndex + length - targetLength - 1, filteredCB, filteredCbIndex + length - 1, energy, energyIndex, energyShifts, energyShiftsIndex, scale, baseSize);
		}

		public static void cbSearch(EncoderState encoderState, EncoderBits encoderBits, CbSearchData searchData, CbUpdateIndexData updateIndexData, short[] inTarget, int inTargetIndex, short[] decResidual, int decResidualIndex, int length, int vectorLength, short[] weightDenum, int weightDenumindex, int blockNumber, int cbIndexIndex, int gainIndexIndex, CbSearchVariables variables,Mode mode)
		{
			variables.reset();
			variables.cbIndex = encoderBits.cbIndex;
			variables.gainIndex = encoderBits.gainIndex;


			System.arraycopy(ILBC.emptyIntArray, 0, variables.cDot, 0, 128); //variables.cDot = new int[128];
			System.arraycopy(ILBC.emptyIntArray, 0, variables.crit, 0, 128); //variables.crit = new int[128];


			variables.baseSize = (short) (length - vectorLength + 1);
			if (vectorLength == 40)
				variables.baseSize = (short) (length - 19);

			variables.numberOfZeroes = length - ILBC.FILTER_RANGE[blockNumber];
			BasicFunctions.filterAR(decResidual, decResidualIndex + variables.numberOfZeroes, variables.buf, 10 + variables.numberOfZeroes, weightDenum, weightDenumindex, 11, ILBC.FILTER_RANGE[blockNumber]);
			System.arraycopy(variables.cbBuf, length, variables.targetVec, 0, 10);

			BasicFunctions.filterAR(inTarget, inTargetIndex, variables.target, 10, weightDenum, weightDenumindex, 11, vectorLength);
			System.arraycopy(variables.target, 10, variables.codedVec, 0, vectorLength);

			variables.currIndex = 10;
			variables.tempS = 0;
			for (variables.i = 0; variables.i < length; variables.i++)
			{
				if (variables.buf[variables.currIndex] > 0 && variables.buf[variables.currIndex] > variables.tempS)
					variables.tempS = variables.buf[variables.currIndex];
				else if ((0 - variables.buf[variables.currIndex]) > variables.tempS)
					variables.tempS = (short) (0 - variables.buf[variables.currIndex]);

				variables.currIndex++;
			}

			variables.currIndex = 10;
			variables.tempS2 = 0;
			for (variables.i = 0; variables.i < vectorLength; variables.i++)
			{
				if (variables.target[variables.currIndex] > 0 && variables.target[variables.currIndex] > variables.tempS2)
					variables.tempS2 = variables.target[variables.currIndex];
				else if ((0 - variables.target[variables.currIndex]) > variables.tempS2)
					variables.tempS2 = (short) (0 - variables.target[variables.currIndex]);

				variables.currIndex++;
			}

			if ((variables.tempS > 0) && (variables.tempS2 > 0))
			{
				if (variables.tempS2 > variables.tempS)
					variables.tempS = variables.tempS2;

				variables.scale = BasicFunctions.getSize(variables.tempS * variables.tempS);
			}
			else variables.scale = 30;

			variables.scale = (short) (variables.scale - 25);
			if (variables.scale < 0)
				variables.scale = 0;

			variables.scale2 = variables.scale;
			variables.targetEner = BasicFunctions.scaleRight(variables.target, 10, variables.target, 10, vectorLength, variables.scale2);

			filteredCBVecs(variables.cbVectors, 0, variables.buf, 10, length, ILBC.FILTER_RANGE[blockNumber]);

			variables.range = ILBC.SEARCH_RANGE[blockNumber][0];
			if (vectorLength == 40)
			{
				interpolateSamples(variables.interpSamples, 0, variables.buf, 10, length, variables.interpolateSamplesVariables);
				interpolateSamples(variables.interSamplesFilt, 0, variables.cbVectors, 0, length, variables.interpolateSamplesVariables);

				cbMemEnergyAugmentation(variables.interpSamples, 0, variables.buf, 10, variables.scale2, (short) 20, variables.energy, 0, variables.energyShifts, 0, variables.cbMemEnergyAugmentationVariables);
				cbMemEnergyAugmentation(variables.interSamplesFilt, 0, variables.cbVectors, 0, variables.scale2, (short) (variables.baseSize + 20), variables.energy, 0, variables.energyShifts, 0, variables.cbMemEnergyAugmentationVariables);

				cbMemEnergy(variables.range, variables.buf, 10, variables.cbVectors, 0, (short) length, (short) vectorLength, variables.energy, 20, variables.energyShifts, 20, variables.scale2, variables.baseSize, variables.cbMemEnergyVariables);
			}
			else cbMemEnergy(variables.range, variables.buf, 10, variables.cbVectors, 0, (short) length, (short) vectorLength, variables.energy, 0, variables.energyShifts, 0, variables.scale2, variables.baseSize, variables.cbMemEnergyVariables);

			energyInverse(variables.energy, 0, variables.baseSize * 2);

			variables.gains[0] = 16384;
			for (variables.stage = 0; variables.stage < 3; variables.stage++)
			{
				variables.range = ILBC.SEARCH_RANGE[blockNumber][variables.stage];

				/* initialize search measures */
				updateIndexData.critMax = 0;
				updateIndexData.shTotMax = (short) -100;
				updateIndexData.bestIndex = (short) 0;
				updateIndexData.bestGain = (short) 0;

				/* Calculate all the cross correlations (augmented part of CB) */
				if (vectorLength == 40)
				{
					augmentCbCorr(variables.target, 10, variables.buf, 10 + length, variables.interpSamples, 0, variables.cDot, 0, 20, 39, variables.scale2);
					variables.currIndex = 20;
				}
				else variables.currIndex = 0;

				crossCorrelation(variables.cDot, variables.currIndex, variables.target, 10, variables.buf, 10 + length - vectorLength, (short) vectorLength, variables.range, variables.scale2, (short) -1, variables.crossCorrelationVariables);

				if (vectorLength == 40)
					variables.range = (short) (ILBC.SEARCH_RANGE[blockNumber][variables.stage] + 20);
				else variables.range = ILBC.SEARCH_RANGE[blockNumber][variables.stage];

				cbSearchCore(searchData, variables.cDot, 0, variables.range, (short) variables.stage, variables.inverseEnergy, 0, variables.inverseEnergyShifts, 0, variables.crit, 0, variables.cbSearchCoreVariables);
				updateBestIndex(updateIndexData, searchData.critNew, searchData.critNewSh, searchData.indexNew, variables.cDot[searchData.indexNew], variables.inverseEnergy[searchData.indexNew], variables.inverseEnergyShifts[searchData.indexNew], variables.updateBestIndexVariables);

				variables.sInd = (short) (updateIndexData.bestIndex - 17);
				variables.eInd = (short) (variables.sInd + 34);

				if (variables.sInd < 0)
				{
					variables.eInd -= variables.sInd;
					variables.sInd = 0;
				}

				if (variables.eInd >= variables.range)
				{
					variables.eInd = (short) (variables.range - 1);
					variables.sInd = (short) (variables.eInd - 34);
				}

				variables.range = ILBC.SEARCH_RANGE[blockNumber][variables.stage];

				if (vectorLength == 40)
				{
					variables.i = variables.sInd;

					if (variables.sInd < 20)
					{
						if (variables.eInd + 20 > 39)
							augmentCbCorr(variables.target, 10, variables.cbVectors, length, variables.interSamplesFilt, 0, variables.cDot, 0, variables.sInd + 20, 39, variables.scale2);
						else augmentCbCorr(variables.target, 10, variables.cbVectors, length, variables.interSamplesFilt, 0, variables.cDot, 0, variables.sInd + 20, variables.eInd + 20, variables.scale2);

						variables.i = 20;
					}

					if (20 - variables.sInd > 0)
						variables.currIndex = 20 - variables.sInd;
					else variables.currIndex = 0;

					crossCorrelation(variables.cDot, variables.currIndex, variables.target, 10, variables.cbVectors, length - 20 - variables.i, (short) vectorLength, (short) (variables.eInd - variables.i + 1), variables.scale2, (short) -1, variables.crossCorrelationVariables);
				}
				else crossCorrelation(variables.cDot, 0, variables.target, 10, variables.cbVectors, length - vectorLength - variables.sInd, (short) vectorLength, (short) (variables.eInd - variables.sInd + 1), variables.scale2, (short) -1, variables.crossCorrelationVariables);

				cbSearchCore(searchData, variables.cDot, 0, (short) (variables.eInd - variables.sInd + 1), (short) variables.stage, variables.inverseEnergy, variables.baseSize + variables.sInd, variables.inverseEnergyShifts, variables.baseSize + variables.sInd, variables.crit, 0, variables.cbSearchCoreVariables);
				updateBestIndex(updateIndexData, searchData.critNew, searchData.critNewSh, (short) (searchData.indexNew + variables.baseSize + variables.sInd), variables.cDot[searchData.indexNew], variables.inverseEnergy[searchData.indexNew + variables.baseSize + variables.sInd], variables.inverseEnergyShifts[searchData.indexNew + variables.baseSize + variables.sInd], variables.updateBestIndexVariables);

				variables.cbIndex[cbIndexIndex + variables.stage] = updateIndexData.bestIndex;
				if (variables.gains[variables.stage] > 0)
					updateIndexData.bestGain = gainQuant(updateIndexData.bestGain, variables.gains[variables.stage], (short) variables.stage, variables.gainIndex, gainIndexIndex + variables.stage, variables.gainQuantVariables);
				else updateIndexData.bestGain = gainQuant(updateIndexData.bestGain, (short) (0 - variables.gains[variables.stage]), (short) variables.stage, variables.gainIndex, gainIndexIndex + variables.stage, variables.gainQuantVariables);

				if (vectorLength == 80 - mode.state_short_len)
				{
					if (variables.cbIndex[cbIndexIndex + variables.stage] < variables.baseSize)
					{
						variables.pp = variables.buf;
						variables.ppIndex = 10 + length - vectorLength - variables.cbIndex[cbIndexIndex + variables.stage];
					}
					else
					{
						variables.pp = variables.cbVectors;
						variables.ppIndex = length - vectorLength - variables.cbIndex[cbIndexIndex + variables.stage] + variables.baseSize;
					}
				}
				else
				{
					if (variables.cbIndex[cbIndexIndex + variables.stage] < variables.baseSize)
					{
						if (variables.cbIndex[cbIndexIndex + variables.stage] >= 20)
						{
							variables.cbIndex[cbIndexIndex + variables.stage] -= 20;
							variables.pp = variables.buf;
							variables.ppIndex = 10 + length - vectorLength - variables.cbIndex[cbIndexIndex + variables.stage];
						}
						else
						{
							variables.cbIndex[cbIndexIndex + variables.stage] += (variables.baseSize - 20);
							createAugmentVector((short) (variables.cbIndex[cbIndexIndex + variables.stage] - variables.baseSize + 40), variables.buf, 10 + length, variables.augVec, 0, variables.createAugmentVectorVariables);
							variables.pp = variables.augVec;
							variables.ppIndex = 0;
						}
					}
					else
					{
						if ((variables.cbIndex[cbIndexIndex + variables.stage] - variables.baseSize) >= 20)
						{
							variables.cbIndex[cbIndexIndex + variables.stage] -= 20;
							variables.pp = variables.cbVectors;
							variables.ppIndex = length - vectorLength - variables.cbIndex[cbIndexIndex + variables.stage] + variables.baseSize;
						}
						else
						{
							variables.cbIndex[cbIndexIndex + variables.stage] += (variables.baseSize - 20);
							createAugmentVector((short) (variables.cbIndex[cbIndexIndex + variables.stage] - 2 * variables.baseSize + 40), variables.cbVectors, length, variables.augVec, 0, variables.createAugmentVectorVariables);
							variables.pp = variables.augVec;
							variables.ppIndex = 0;
						}
					}
				}

				BasicFunctions.addAffineVectorToVector(variables.target, 10, variables.pp, variables.ppIndex, (short) (0 - updateIndexData.bestGain), 8192, (short) 14, vectorLength);
				variables.gains[variables.stage + 1] = updateIndexData.bestGain;
			}

			variables.currIndex = 10;
			for (variables.i = 0; variables.i < vectorLength; variables.i++)
				variables.codedVec[variables.i] -= variables.target[variables.currIndex++];

			variables.codedEner = BasicFunctions.scaleRight(variables.codedVec, 0, variables.codedVec, 0, vectorLength, variables.scale2);

			variables.j = variables.gainIndex[gainIndexIndex];

			variables.tempS = BasicFunctions.norm(variables.codedEner);
			variables.tempS2 = BasicFunctions.norm(variables.targetEner);

			if (variables.tempS < variables.tempS2)
				variables.nBits = (short) (16 - variables.tempS);
			else variables.nBits = (short) (16 - variables.tempS2);

			if (variables.nBits < 0)
				variables.targetEner = (variables.targetEner << (0 - variables.nBits)) * ((variables.gains[1] * variables.gains[1]) >> 14);
			else variables.targetEner = (variables.targetEner >> variables.nBits) * ((variables.gains[1] * variables.gains[1]) >> 14);

			variables.gainResult = ((variables.gains[1] - 1) << 1);

			if (variables.nBits < 0)
				variables.tempS = (short) (variables.codedEner << (-variables.nBits));
			else variables.tempS = (short) (variables.codedEner >> variables.nBits);

			for (variables.i = variables.gainIndex[gainIndexIndex]; variables.i < 32; variables.i++)
			{
				if ((variables.tempS * ILBC.GAIN_SQ5_SQ[variables.i] - variables.targetEner) < 0 && ILBC.GAIN_SQ5[variables.j] < variables.gainResult)
					variables.j = variables.i;
			}

			variables.gainIndex[gainIndexIndex] = (short) variables.j;
		}

		public static void cbSearchCore(CbSearchData searchData, int[] cDot, int cDotIndex, short range, short stage, short[] inverseEnergy, int inverseEnergyIndex, short[] inverseEnergyShift, int inverseEnergyShiftIndex, int[] crit, int critIndex, CbSearchCoreVariables variables)
		{
			if (stage == 0)
			{
				for (variables.n = 0; variables.n < range; variables.n++)
				{
					if (cDot[cDotIndex] < 0)
						cDot[cDotIndex] = 0;

					cDotIndex++;
				}
				cDotIndex -= range;
			}

			variables.current = 0;
			for (variables.n = 0; variables.n < range; variables.n++)
			{
				if (cDot[cDotIndex] > 0 && cDot[cDotIndex] > variables.current)
					variables.current = cDot[cDotIndex];
				else if ((0 - cDot[cDotIndex]) > variables.current)
					variables.current = 0 - cDot[cDotIndex];

				cDotIndex++;
			}

			cDotIndex -= range;
			variables.nBits = BasicFunctions.norm(variables.current);
			variables.max = Short.MIN_VALUE;

			for (variables.n = 0; variables.n < range; variables.n++)
			{
				variables.tempS = (short) ((cDot[cDotIndex++] << variables.nBits) >> 16);
				crit[critIndex] = ((variables.tempS * variables.tempS) >> 16) * inverseEnergy[inverseEnergyIndex++];

				if (crit[critIndex] != 0 && inverseEnergyShift[inverseEnergyShiftIndex] > variables.max)
					variables.max = inverseEnergyShift[inverseEnergyShiftIndex];

				inverseEnergyShiftIndex++;
				critIndex++;
			}

			if (variables.max == Short.MIN_VALUE)
				variables.max = 0;

			critIndex -= range;
			inverseEnergyShiftIndex -= range;
			for (variables.n = 0; variables.n < range; variables.n++)
			{
				if (variables.max - inverseEnergyShift[inverseEnergyShiftIndex] > 16)
					crit[critIndex] = crit[critIndex] >> 16;
				else
				{
					variables.tempS = (short) (variables.max - inverseEnergyShift[inverseEnergyShiftIndex]);
					if (variables.tempS < 0)
						crit[critIndex] <<= -variables.tempS;
					else crit[critIndex] >>= variables.tempS;
				}

				inverseEnergyShiftIndex++;
				critIndex++;
			}

			critIndex -= range;
			variables.maxCrit = crit[critIndex];
			critIndex++;
			searchData.indexNew = (short) 0;
			for (variables.n = 1; variables.n < range; variables.n++)
			{
				if (crit[critIndex] > variables.maxCrit)
				{
					variables.maxCrit = crit[critIndex];
					searchData.indexNew = (short) variables.n;
				}

				critIndex++;
			}

			searchData.critNew = variables.maxCrit;
			searchData.critNewSh = (short) (32 - 2 * variables.nBits + variables.max);
		}

		public static void cbConstruct(EncoderBits encoderBits, short[] decVector, int decVectorIndex, short[] mem, int memIndex, short length, short vectorLength, int cbIndexIndex, int gainIndexIndex, CbConstructVariables variables)
		{
			variables.reset();
			variables.cbIndex = encoderBits.cbIndex;
			variables.gainIndex = encoderBits.gainIndex;

			variables.gain[0] = gainDequant(variables.gainIndex[gainIndexIndex], (short) 16384, (short) 0);
			variables.gain[1] = gainDequant(variables.gainIndex[gainIndexIndex + 1], (short) variables.gain[0], (short) 1);
			variables.gain[2] = gainDequant(variables.gainIndex[gainIndexIndex + 2], (short) variables.gain[1], (short) 2);

			System.arraycopy(ILBC.emptyArray, 0, variables.cbVec0, 0, 40);
			System.arraycopy(ILBC.emptyArray, 0, variables.cbVec1, 0, 40);
			System.arraycopy(ILBC.emptyArray, 0, variables.cbVec2, 0, 40);

			getCbVec(variables.cbVec0, 0, mem, memIndex, variables.cbIndex[cbIndexIndex], length, vectorLength, variables.getCbVecVariables);
			getCbVec(variables.cbVec1, 0, mem, memIndex, variables.cbIndex[cbIndexIndex + 1], length, vectorLength, variables.getCbVecVariables);
			getCbVec(variables.cbVec2, 0, mem, memIndex, variables.cbIndex[cbIndexIndex + 2], length, vectorLength, variables.getCbVecVariables);

			for (variables.i = 0; variables.i < vectorLength; variables.i++)
				decVector[decVectorIndex++] = (short) ((variables.gain[0] * variables.cbVec0[variables.i] + variables.gain[1] * variables.cbVec1[variables.i] + variables.gain[2] * variables.cbVec2[variables.i] + 8192) >> 14);
		}

		public static void stateSearch(EncoderState encoderState, EncoderBits encoderBits, short[] residual, int residualIndex, short[] syntDenum, int syntIndex, short[] weightDenum, int weightIndex, StateSearchVariables variables,Mode mode)
		{
			variables.reset();
			variables.max = 0;

			for (variables.n = 0; variables.n < mode.state_short_len; variables.n++)
			{
				variables.tempS = residual[residualIndex++];
				if (variables.tempS < 0)
					variables.tempS = (short) (0 - variables.tempS);

				if (variables.tempS > variables.max)
					variables.max = variables.tempS;
			}

			variables.tempS = (short) (BasicFunctions.getSize(variables.max) - 12);
			if (variables.tempS < 0)
				variables.tempS = 0;

			variables.currIndex = syntIndex + 10;
			for (variables.n = 0; variables.n < 11; variables.n++)
				variables.numerator[variables.n] = (short) (syntDenum[variables.currIndex--] >> variables.tempS);

			residualIndex -= mode.state_short_len;
			System.arraycopy(residual, residualIndex, variables.residualLong, 10, mode.state_short_len);
			System.arraycopy(ILBC.emptyArray, 0, variables.residualLong, 10 + mode.state_short_len, mode.state_short_len);
			System.arraycopy(ILBC.emptyArray, 0, variables.residualLongVec, 0, 10);
			BasicFunctions.filterMA(variables.residualLong, 10, variables.sampleMa, 0, variables.numerator, 0, 11, mode.state_short_len + 10);

			System.arraycopy(ILBC.emptyArray, 0, variables.sampleMa, mode.state_short_len + 10, mode.state_short_len - 10);
			BasicFunctions.filterAR(variables.sampleMa, 0, variables.sampleAr, 10, syntDenum, syntIndex, 11, 2 * mode.state_short_len);

			int arIndex = 10;
			int arIndex2 = 10 + mode.state_short_len;
			for (variables.n = 0; variables.n < mode.state_short_len; variables.n++)
				variables.sampleAr[arIndex++] += variables.sampleAr[arIndex2++];

			variables.max = 0;
			arIndex = 10;
			for (variables.n = 0; variables.n < mode.state_short_len; variables.n++)
			{
				variables.tempS2 = variables.sampleAr[arIndex++];
				if (variables.tempS2 < 0)
					variables.tempS2 = (short) (0 - variables.tempS2);

				if (variables.tempS2 > variables.max)
					variables.max = variables.tempS2;
			}

			/* Find the best index */
			if ((variables.max << variables.tempS) < 23170)
				variables.temp = (variables.max * variables.max) << (2 + 2 * variables.tempS);
			else variables.temp = Integer.MAX_VALUE;

			variables.currIndex = 0;
			for (variables.n = 0; variables.n < 63; variables.n++)
			{
				if (variables.temp >= ILBC.CHOOSE_FRG_QUANT[variables.n])
					variables.currIndex = variables.n + 1;
				else variables.n = 63;
			}

			encoderBits.idxForMax = (short) variables.currIndex;
			if (variables.currIndex < 27)
				variables.nBits = 4;
			else variables.nBits = 9;

			BasicFunctions.scaleVector(variables.sampleAr, 10, variables.sampleAr, 10, ILBC.SCALE[variables.currIndex], mode.state_short_len, variables.nBits - variables.tempS);
			absQuant(encoderBits, variables.sampleAr, 10, weightDenum, weightIndex, variables.absQuantVariables,mode);
		}

		public static void stateConstruct(EncoderBits encoderBits, short[] syntDenum, int syntDenumIndex, short[] outFix, int outFixIndex, int stateLen, StateConstructVariables variables)
		{
			variables.reset();
			variables.idxVec = encoderBits.idxVec;
			variables.currIndex = syntDenumIndex + 10;

			for (variables.k = 0; variables.k < 11; variables.k++)
				variables.numerator[variables.k] = syntDenum[variables.currIndex--];

			variables.max = ILBC.FRQ_QUANT_MOD[encoderBits.idxForMax];
			if (encoderBits.idxForMax < 37)
			{
				variables.coef = 2097152;
				variables.bitShift = 22;
			}
			else if (encoderBits.idxForMax < 59)
			{
				variables.coef = 262144;
				variables.bitShift = 19;
			}
			else
			{
				variables.coef = 65536;
				variables.bitShift = 17;
			}

			variables.currIndex = 10;
			variables.currIndex2 = stateLen - 1;
			for (variables.k = 0; variables.k < stateLen; variables.k++)
				variables.sampleVal[variables.currIndex++] = (short) ((variables.max * ILBC.STATE_SQ3[variables.idxVec[variables.currIndex2--]] + variables.coef) >> variables.bitShift);

			System.arraycopy(ILBC.emptyArray, 0, variables.sampleVal, 10 + stateLen, stateLen);
			System.arraycopy(ILBC.emptyArray, 0, variables.sampleValVec, 0, 10);
			BasicFunctions.filterMA(variables.sampleVal, 10, variables.sampleMa, 10, variables.numerator, 0, 11, 11 + stateLen);
			System.arraycopy(ILBC.emptyArray, 0, variables.sampleMa, 20 + stateLen, stateLen - 10);
			BasicFunctions.filterAR(variables.sampleMa, 10, variables.sampleAr, 10, syntDenum, syntDenumIndex, 11, 2 * stateLen);

			variables.currIndex = 10 + stateLen - 1;
			variables.currIndex2 = 10 + 2 * stateLen - 1;
			for (variables.k = 0; variables.k < stateLen; variables.k++)
				outFix[outFixIndex++] = (short) (variables.sampleAr[variables.currIndex--] + variables.sampleAr[variables.currIndex2--]);
		}

		public static void filteredCBVecs(short[] cbVectors, int cbVectorsIndex, short[] cbMem, int cbMemIndex, int length, int samples)
		{
			System.arraycopy(ILBC.emptyArray, 0, cbMem, cbMemIndex + length, 4);
			System.arraycopy(ILBC.emptyArray, 0, cbMem, cbMemIndex - 4, 4);
			System.arraycopy(ILBC.emptyArray, 0, cbVectors, cbVectorsIndex, length - samples);

			BasicFunctions.filterMA(cbMem, cbMemIndex + 4 + length - samples, cbVectors, cbVectorsIndex + length - samples, ILBC.CB_FILTERS_REV, 0, 8, samples);
		}

		public static void crossCorrelation(int[] crossCorrelation, int crossCorrelationIndex, short[] seq1, int seq1Index, short[] seq2, int seq2Index, short dimSeq, short dimCrossCorrelation, short rightShifts, short stepSeq2, CrossCorrelationVariables variables)
		{
			for (variables.i = 0; variables.i < dimCrossCorrelation; variables.i++)
			{
				crossCorrelation[crossCorrelationIndex] = 0;

				for (variables.j = 0; variables.j < dimSeq; variables.j++)
					crossCorrelation[crossCorrelationIndex] += (seq1[seq1Index++] * seq2[seq2Index++]) >> rightShifts;

				seq1Index -= dimSeq;
				seq2Index = seq2Index + stepSeq2 - dimSeq;
				crossCorrelationIndex++;
			}
		}

		public static void updateBestIndex(CbUpdateIndexData updateIndexData, int critNew, short critNewSh, short indexNew, int cDotNew, short inverseEnergyNew, short energyShiftNew, UpdateBestIndexVariables variables)
		{
			variables.current = critNewSh - updateIndexData.shTotMax;
			if (variables.current > 31)
			{
				variables.shOld = 31;
				variables.shNew = 0;
			}
			else if (variables.current > 0)
			{
				variables.shOld = variables.current;
				variables.shNew = 0;
			}
			else if (variables.current > -31)
			{
				variables.shNew = 0 - variables.current;
				variables.shOld = 0;
			}
			else
			{
				variables.shNew = 31;
				variables.shOld = 0;
			}

			if ((critNew >> variables.shNew) > (updateIndexData.critMax >> variables.shOld))
			{
				variables.tempShort = (short) (16 - BasicFunctions.norm(cDotNew));
				variables.tempScale = (short) (31 - energyShiftNew - variables.tempShort);
				if (variables.tempScale > 31)
					variables.tempScale = 31;

				if (variables.tempShort < 0)
					variables.gain = ((cDotNew << (-variables.tempShort)) * inverseEnergyNew) >> variables.tempScale;
				else variables.gain = ((cDotNew >> variables.tempShort) * inverseEnergyNew) >> variables.tempScale;

				if (variables.gain > ILBC.bestIndexMaxI)
					updateIndexData.bestGain = ILBC.bestIndexMax;
				else if (variables.gain < ILBC.bestIndexMinI)
					updateIndexData.bestGain = ILBC.bestIndexMin;
				else updateIndexData.bestGain = (short) variables.gain;

				updateIndexData.critMax = critNew;
				updateIndexData.shTotMax = critNewSh;
				updateIndexData.bestIndex = indexNew;
			}
		}

		public static void getCbVec(short[] cbVec, int cbVecIndex, short[] mem, int memIndex, short index, int length, int vectorLength, GetCbVecVariables variables)
		{
			variables.reset();
			variables.baseSize = (short) (length - vectorLength + 1);

			if (vectorLength == 40)
				variables.baseSize += vectorLength >> 1;

			if (index < length - vectorLength + 1)
			{
				variables.k = index + vectorLength;
				System.arraycopy(mem, memIndex + length - variables.k, cbVec, cbVecIndex, vectorLength);
			}
			else if (index < variables.baseSize)
			{
				variables.k = 2 * (index - (length - vectorLength + 1)) + vectorLength;
				createAugmentVector((short) (variables.k >> 1), mem, memIndex + length, cbVec, cbVecIndex, variables.createAugmentVectorVariables);
			}
			else
			{
				if (index - variables.baseSize < length - vectorLength + 1)
				{
					System.arraycopy(ILBC.emptyArray, 0, mem, memIndex - 4, 4);
					System.arraycopy(ILBC.emptyArray, 0, mem, memIndex + length, 4);
					BasicFunctions.filterMA(mem, memIndex + length - (index - variables.baseSize + vectorLength) + 4, cbVec, cbVecIndex, ILBC.CB_FILTERS_REV, 0, 8, vectorLength);
				}
				else
				{
					System.arraycopy(ILBC.emptyArray, 0, mem, memIndex + length, 4);
					BasicFunctions.filterMA(mem, memIndex + length - vectorLength - 1, variables.tempBuffer, 0, ILBC.CB_FILTERS_REV, 0, 8, vectorLength + 5);
					createAugmentVector((short) ((vectorLength << 1) - 20 + index - variables.baseSize - length - 1), variables.tempBuffer, 45, cbVec, cbVecIndex, variables.createAugmentVectorVariables);
				}
			}
		}

		public static void createAugmentVector(short index, short[] buf, int bufIndex, short[] cbVec, int cbVecIndex, CreateAugmentVectorVariables variables)
		{
			variables.reset();
			variables.currIndex = cbVecIndex + index - 4;

			System.arraycopy(buf, bufIndex - index, cbVec, cbVecIndex, index);
			BasicFunctions.multWithRightShift(cbVec, variables.currIndex, buf, bufIndex - index - 4, ILBC.ALPHA, 0, 4, 15);
			BasicFunctions.reverseMultiplyRight(variables.cbVecTmp, 0, buf, bufIndex - 4, ILBC.ALPHA, 3, 4, 15);
			BasicFunctions.addWithRightShift(cbVec, variables.currIndex, cbVec, variables.currIndex, variables.cbVecTmp, 0, 4, 0);
			System.arraycopy(buf, bufIndex - index, cbVec, cbVecIndex + index, 40 - index);
		}

		public static void energyInverse(short[] energy, int energyIndex, int length)
		{
			int n;
			for (n = 0; n < length; n++)
			{
				if (energy[energyIndex] < 16384)
					energy[energyIndex++] = Short.MAX_VALUE;
				else
				{
					energy[energyIndex] = (short) (0x1FFFFFFF / energy[energyIndex]);
					energyIndex++;
				}
			}
		}

		public static void energyCalc(int energy, short range, short[] ppi, int ppiIndex, short[] ppo, int ppoIndex, short[] energyArray, int energyArrayIndex, short[] energyShifts, int energyShiftsIndex, short scale, short baseSize)
		{
			int n;

			energyShiftsIndex += 1 + baseSize;
			energyArrayIndex += 1 + baseSize;

			for (n = 0; n < range - 1; n++)
			{
				energy += ((ppi[ppiIndex] * ppi[ppiIndex]) - (ppo[ppoIndex] * ppo[ppoIndex])) >> scale;
				if (energy < 0)
					energy = 0;

				ppiIndex--;
				ppoIndex--;

				energyShifts[energyShiftsIndex] = BasicFunctions.norm(energy);
				energyArray[energyArrayIndex++] = (short) ((energy << energyShifts[energyShiftsIndex]) >> 16);
				energyShiftsIndex++;
			}
		}

		public static void augmentCbCorr(short[] target, int targetIndex, short[] buf, int bufIndex, short[] interpSamples, int interpSamplesIndex, int[] cDot, int cDotIndex, int low, int high, int scale)
		{
			int n;
			for (n = low; n <= high; n++)
			{
				cDot[cDotIndex] = BasicFunctions.scaleRight(target, targetIndex, buf, bufIndex - n, n - 4, scale);

				cDot[cDotIndex] += BasicFunctions.scaleRight(target, targetIndex + n - 4, interpSamples, interpSamplesIndex, 4, scale);
				interpSamplesIndex += 4;

				cDot[cDotIndex] += BasicFunctions.scaleRight(target, targetIndex + n, buf, bufIndex - n, 40 - n, scale);
				cDotIndex++;
			}
		}

		public static void absQuant(EncoderBits encoderBits, short[] in, int inIndex, short[] weightDenum, int weightDenumIndex, AbsQuantVariables variables,Mode mode)
		{
			variables.reset();
			if (encoderBits.stateFirst)
			{
				variables.quantLen[0] = 40;
				variables.quantLen[1] = (short) (mode.state_short_len - 40);
			}
			else
			{
				variables.quantLen[0] = (short) (mode.state_short_len - 40);
				variables.quantLen[1] = 40;
			}

			BasicFunctions.filterAR(in, inIndex, variables.inWeighted, 10, weightDenum, weightDenumIndex, 11, variables.quantLen[0]);
			BasicFunctions.filterAR(in, inIndex + variables.quantLen[0], variables.inWeighted, 10 + variables.quantLen[0], weightDenum, weightDenumIndex + 11, 11, variables.quantLen[1]);

			absQUantLoop(encoderBits, variables.syntOutBuf, 10, variables.inWeighted, 10, weightDenum, weightDenumIndex, variables.quantLen, 0, variables.absQuantLoopVariables);
		}

		public static void absQUantLoop(EncoderBits encoderBits, short[] syntOut, int syntOutIndex, short[] inWeighted, int inWeightedIndex, short[] weightDenum, int weightDenumIndex, short[] quantLen, int quantLenIndex, AbsQuantLoopVariables variables)
		{
			variables.idxVec = encoderBits.idxVec;
			variables.startIndex = 0;

			for (variables.i = 0; variables.i < 2; variables.i++)
			{
				variables.currIndex = quantLenIndex + variables.i;
				for (variables.j = 0; variables.j < quantLen[variables.currIndex]; variables.j++)
				{
					BasicFunctions.filterAR(syntOut, syntOutIndex, syntOut, syntOutIndex, weightDenum, weightDenumIndex, 11, 1);

					variables.temp = inWeighted[inWeightedIndex] - syntOut[syntOutIndex];
					variables.temp2 = variables.temp << 2;

					if (variables.temp2 > Short.MAX_VALUE)
						variables.temp2 = Short.MAX_VALUE;
					else if (variables.temp2 < Short.MIN_VALUE)
						variables.temp2 = Short.MIN_VALUE;

					if (variables.temp < -7577)
					{
						variables.idxVec[variables.startIndex + variables.j] = (short) 0;
						syntOut[syntOutIndex++] = (short) (((ILBC.STATE_SQ3[0] + 2) >> 2) + inWeighted[inWeightedIndex++] - variables.temp);
					}
					else if (variables.temp > 8151)
					{
						variables.idxVec[variables.startIndex + variables.j] = (short) 7;
						syntOut[syntOutIndex++] = (short) (((ILBC.STATE_SQ3[7] + 2) >> 2) + inWeighted[inWeightedIndex++] - variables.temp);
					}
					else
					{
						if (variables.temp2 <= ILBC.STATE_SQ3[0])
						{
							variables.idxVec[variables.startIndex + variables.j] = (short) 0;
							syntOut[syntOutIndex++] = (short) (((ILBC.STATE_SQ3[0] + 2) >> 2) + inWeighted[inWeightedIndex++] - variables.temp);
						}
						else
						{
							variables.k = 0;
							while (variables.temp2 > ILBC.STATE_SQ3[variables.k] && variables.k < ILBC.STATE_SQ3.length - 1)
								variables.k++;

							if (variables.temp2 > ((ILBC.STATE_SQ3[variables.k] + ILBC.STATE_SQ3[variables.k - 1] + 1) >> 1))
							{
								variables.idxVec[variables.startIndex + variables.j] = (short) variables.k;
								syntOut[syntOutIndex++] = (short) (((ILBC.STATE_SQ3[variables.k] + 2) >> 2) + inWeighted[inWeightedIndex++] - variables.temp);
							}
							else
							{
								variables.idxVec[variables.startIndex + variables.j] = (short) (variables.k - 1);
								syntOut[syntOutIndex++] = (short) (((ILBC.STATE_SQ3[variables.k - 1] + 2) >> 2) + inWeighted[inWeightedIndex++] - variables.temp);
							}
						}
					}

				}

				variables.startIndex += quantLen[variables.currIndex];
				weightDenumIndex += 11;
			}
		}

		public static void interpolateSamples(short[] interpSamples, int interpSamplesIndex, short[] cbMem, int cbMemIndex, int length, InterpolateSamplesVariables variables)
		{
			for (variables.n = 0; variables.n < 20; variables.n++)
			{
				variables.highIndex = cbMemIndex + length - 4;
				variables.lowIndex = cbMemIndex + length - variables.n - 24;

				interpSamples[interpSamplesIndex++] = (short) (((ILBC.ALPHA[3] * cbMem[variables.highIndex++]) >> 15) + ((ILBC.ALPHA[0] * cbMem[variables.lowIndex++]) >> 15));
				interpSamples[interpSamplesIndex++] = (short) (((ILBC.ALPHA[2] * cbMem[variables.highIndex++]) >> 15) + ((ILBC.ALPHA[1] * cbMem[variables.lowIndex++]) >> 15));
				interpSamples[interpSamplesIndex++] = (short) (((ILBC.ALPHA[1] * cbMem[variables.highIndex++]) >> 15) + ((ILBC.ALPHA[2] * cbMem[variables.lowIndex++]) >> 15));
				interpSamples[interpSamplesIndex++] = (short) (((ILBC.ALPHA[0] * cbMem[variables.highIndex++]) >> 15) + ((ILBC.ALPHA[3] * cbMem[variables.lowIndex++]) >> 15));
			}
		}

		public static short frameClassify(short[] residual, FrameClassifyVariables variables,Mode mode)
		{
			variables.reset();
			variables.max = 0;

			for (variables.n = 0; variables.n < mode.size; variables.n++)
			{
				variables.tempS = residual[variables.n];
				if (variables.tempS < 0)
					variables.tempS = (short) (0 - variables.tempS);

				if (variables.tempS > variables.max)
					variables.max = variables.tempS;
			}

			variables.scale = (short) (BasicFunctions.getSize(variables.max * variables.max) - 24);
			if (variables.scale < 0)
				variables.scale = 0;

			variables.ssqIndex = 2;
			variables.currIndex = 0;
			for (variables.n = mode.subframes - 1; variables.n > 0; variables.n--)
			{
				variables.ssqEn[variables.currIndex++] = BasicFunctions.scaleRight(residual, variables.ssqIndex, residual, variables.ssqIndex, 76, variables.scale);
				variables.ssqIndex += 40;
			}

			/* Scale to maximum 20 bits in order to allow for the 11 bit window */
			variables.ssqIndex = 0;
			int maxSSq = variables.ssqEn[0];
			for (variables.n = 1; variables.n < mode.subframes - 1; variables.n++)
			{
				if (variables.ssqEn[variables.n] > maxSSq)
					maxSSq = variables.ssqEn[variables.n];
			}

			variables.scale = (short) (BasicFunctions.getSize(maxSSq) - 20);
			if (variables.scale < 0)
				variables.scale = 0;

			variables.ssqIndex = 0;
			variables.currIndex = 1;

			for (variables.n = mode.subframes - 1; variables.n > 0; variables.n--)
			{
				variables.ssqEn[variables.ssqIndex] = (variables.ssqEn[variables.ssqIndex] >> variables.scale) * ILBC.ENG_START_SEQUENCE[variables.currIndex++];
				variables.ssqIndex++;
			}

			/* Extract the best choise of start state */
			variables.currIndex = 0;
			maxSSq = variables.ssqEn[0];
			for (variables.n = 1; variables.n < mode.subframes - 1; variables.n++)
			{
				if (variables.ssqEn[variables.n] > maxSSq)
				{
					variables.currIndex = variables.n;
					maxSSq = variables.ssqEn[variables.n];
				}
			}

			return (short) (variables.currIndex + 1);
		}

		public static void packBits(EncoderState encoderState, EncoderBits encoderBits, byte[] result, PackBitsVariables variables,Mode mode)
		{
			variables.lsf = encoderBits.LSF;
			variables.cbIndex = encoderBits.cbIndex;
			variables.gainIndex = encoderBits.gainIndex;
			variables.idxVec = encoderBits.idxVec;

			result[0] = (byte) ((variables.lsf[0] << 2) | ((variables.lsf[1] >> 5) & 0x3));
			result[1] = (byte) ((variables.lsf[1] & 0x1F) << 3 | ((variables.lsf[2] >> 4) & 0x7));
			result[2] = (byte) ((variables.lsf[2] & 0xF) << 4);

			if (mode == Mode.MODE_20)
			{
				if (encoderBits.stateFirst)
					result[2] |= (encoderBits.startIdx & 0x3) << 2 | 0x2;
				else result[2] |= (encoderBits.startIdx & 0x3) << 2;

				result[2] |= (encoderBits.idxForMax >> 5) & 0x1;
				result[3] = (byte) (((encoderBits.idxForMax & 0x1F) << 3) | ((variables.cbIndex[0] >> 4) & 0x7));
				result[4] = (byte) (((variables.cbIndex[0] & 0xE) << 4) | (variables.gainIndex[0] & 0x18) | ((variables.gainIndex[1] & 0x8) >> 1) | ((variables.cbIndex[3] >> 6) & 0x3));
				result[5] = (byte) (((variables.cbIndex[3] & 0x3E) << 2) | ((variables.gainIndex[3] >> 2) & 0x4) | ((variables.gainIndex[4] >> 2) & 0x2) | ((variables.gainIndex[6] >> 4) & 0x1));
				variables.resIndex = 6;
			}
			else
			{
				result[2] |= (variables.lsf[3] >> 2) & 0xF;
				result[3] = (byte) (((variables.lsf[3] & 0x3) << 6) | ((variables.lsf[4] >> 1) & 0x3F));
				result[4] = (byte) (((variables.lsf[4] & 0x1) << 7) | (variables.lsf[5] & 0x7F));
				if (encoderBits.stateFirst)
					result[5] = (byte) ((encoderBits.startIdx << 5) | 0x10 | (encoderBits.idxForMax >> 2) & 0xF);
				else result[5] = (byte) ((encoderBits.startIdx << 5) | (encoderBits.idxForMax >> 2) & 0xF);

				result[6] = (byte) (((encoderBits.idxForMax & 0x3) << 6) | ((variables.cbIndex[0] & 0x78) >> 1) | ((variables.gainIndex[0] & 0x10) >> 3) | ((variables.gainIndex[1] & 0x80) >> 3));
				result[7] = (byte) (variables.cbIndex[3] & 0xFC | ((variables.gainIndex[3] & 0x10) >> 3) | ((variables.gainIndex[4] & 0x80) >> 3));
				variables.resIndex = 8;
			}

			variables.idxVecIndex = 0;
			for (variables.k = 0; variables.k < 7; variables.k++)
			{
				result[variables.resIndex] = 0;
				for (variables.i = 7; variables.i >= 0; variables.i--)
					result[variables.resIndex] |= (((variables.idxVec[variables.idxVecIndex++] & 0x4) >> 2) << variables.i);

				variables.resIndex++;
			}

			result[variables.resIndex] = (byte) ((variables.idxVec[variables.idxVecIndex++] & 0x4) << 5);

			if (mode == Mode.MODE_20)
			{
				result[variables.resIndex] |= (variables.gainIndex[1] & 0x4) << 4;
				result[variables.resIndex] |= (variables.gainIndex[3] & 0xC) << 2;
				result[variables.resIndex] |= (variables.gainIndex[4] & 0x4) << 1;
				result[variables.resIndex] |= (variables.gainIndex[6] & 0x8) >> 1;
				result[variables.resIndex] |= (variables.gainIndex[7] & 0xC) >> 2;
			}
			else
			{
				result[variables.resIndex] |= (variables.idxVec[variables.idxVecIndex++] & 0x4) << 4;
				result[variables.resIndex] |= (variables.cbIndex[0] & 0x6) << 3;
				result[variables.resIndex] |= (variables.gainIndex[0] & 0x8);
				result[variables.resIndex] |= (variables.gainIndex[1] & 0x4);
				result[variables.resIndex] |= (variables.cbIndex[3] & 0x2);
				result[variables.resIndex] |= (variables.cbIndex[6] & 0x80) >> 7;

				variables.resIndex++;
				result[variables.resIndex] = (byte) ((variables.cbIndex[6] & 0x7E) << 1 | (variables.cbIndex[9] & 0xC0) >> 6);

				variables.resIndex++;
				result[variables.resIndex] = (byte) ((variables.cbIndex[9] & 0x3E) << 2 | (variables.cbIndex[12] & 0xE0) >> 5);

				variables.resIndex++;
				result[variables.resIndex] = (byte) ((variables.cbIndex[12] & 0x1E) << 3 | (variables.gainIndex[3] & 0xC) | (variables.gainIndex[4] & 0x6) >> 1);

				variables.resIndex++;
				result[variables.resIndex] = (byte) ((variables.gainIndex[6] & 0x18) << 3 | (variables.gainIndex[7] & 0xC) << 2 | (variables.gainIndex[9] & 0x10) >> 1 | (variables.gainIndex[10] & 0x8) >> 1 | (variables.gainIndex[12] & 0x10) >> 3 | (variables.gainIndex[13] & 0x8) >> 3);
			}

			variables.idxVecIndex = 0;
			variables.resIndex++;
			for (variables.k = 0; variables.k < 14; variables.k++)
			{
				result[variables.resIndex] = 0;

				for (variables.i = 6; variables.i >= 0; variables.i -= 2)
					result[variables.resIndex] |= ((variables.idxVec[variables.idxVecIndex++] & 0x3) << variables.i);

				variables.resIndex++;
			}

			if (mode == Mode.MODE_20)
			{
				result[variables.resIndex++] = (byte) ((variables.idxVec[56] & 0x3) << 6 | (variables.cbIndex[0] & 0x1) << 5 | (variables.cbIndex[1] & 0x7C) >> 2);
				result[variables.resIndex++] = (byte) (((variables.cbIndex[1] & 0x3) << 6) | ((variables.cbIndex[2] >> 1) & 0x3F));

				result[variables.resIndex++] = (byte) ((variables.cbIndex[2] & 0x1) << 7 | (variables.gainIndex[0] & 0x7) << 4 | (variables.gainIndex[1] & 0x3) << 2 | (variables.gainIndex[2] & 0x6) >> 1);
				result[variables.resIndex++] = (byte) ((variables.gainIndex[2] & 0x1) << 7 | (variables.cbIndex[3] & 0x1) << 6 | (variables.cbIndex[4] & 0x7E) >> 1);

				result[variables.resIndex++] = (byte) ((variables.cbIndex[4] & 0x1) << 7 | (variables.cbIndex[5] & 0x7F));
				result[variables.resIndex++] = (byte) (variables.cbIndex[6] & 0xFF);

				result[variables.resIndex++] = (byte) (variables.cbIndex[7] & 0xFF);
				result[variables.resIndex++] = (byte) (variables.cbIndex[8] & 0xFF);

				result[variables.resIndex++] = (byte) (((variables.gainIndex[3] & 0x3) << 6) | ((variables.gainIndex[4] & 0x3) << 4) | ((variables.gainIndex[5] & 0x7) << 1) | ((variables.gainIndex[6] & 0x4) >> 2));
				result[variables.resIndex++] = (byte) (((variables.gainIndex[6] & 0x3) << 6) | ((variables.gainIndex[7] & 0x3) << 4) | ((variables.gainIndex[8] & 0x7) << 1));
			}
			else
			{
				result[variables.resIndex++] = (byte) ((variables.idxVec[56] & 0x3) << 6 | (variables.idxVec[57] & 0x3) << 4 | (variables.cbIndex[0] & 0x1) << 3 | (variables.cbIndex[1] & 0x70) >> 4);
				result[variables.resIndex++] = (byte) ((variables.cbIndex[1] & 0xF) << 4 | (variables.cbIndex[2] & 0x78) >> 3);

				result[variables.resIndex++] = (byte) ((variables.cbIndex[2] & 0x7) << 5 | (variables.gainIndex[0] & 0x7) << 2 | (variables.gainIndex[1] & 0x3));
				result[variables.resIndex++] = (byte) ((variables.gainIndex[2] & 0x7) << 7 | (variables.cbIndex[3] & 0x1) << 4 | (variables.cbIndex[4] & 0x78) >> 3);

				result[variables.resIndex++] = (byte) ((variables.cbIndex[4] & 0x7) << 5 | (variables.cbIndex[5] & 0x7C) >> 2);
				result[variables.resIndex++] = (byte) ((variables.cbIndex[5] & 0x3) << 6 | (variables.cbIndex[6] & 0x1) << 1 | (variables.cbIndex[7] & 0xF8) >> 3);

				result[variables.resIndex++] = (byte) ((variables.cbIndex[7] & 0x7) << 5 | (variables.cbIndex[8] & 0xF8) >> 3);
				result[variables.resIndex++] = (byte) ((variables.cbIndex[8] & 0x7) << 5 | (variables.cbIndex[9] & 0x1) << 4 | (variables.cbIndex[10] & 0xF0) >> 4);

				result[variables.resIndex++] = (byte) ((variables.cbIndex[10] & 0xF) << 4 | (variables.cbIndex[11] & 0xF0) >> 4);
				result[variables.resIndex++] = (byte) ((variables.cbIndex[11] & 0xF) << 4 | (variables.cbIndex[12] & 0x1) << 3 | (variables.cbIndex[13] & 0xE0) >> 5);

				result[variables.resIndex++] = (byte) ((variables.cbIndex[13] & 0x1F) << 3 | (variables.cbIndex[14] & 0xE0) >> 5);
				result[variables.resIndex++] = (byte) ((variables.cbIndex[14] & 0x1F) << 3 | (variables.gainIndex[3] & 0x3) << 1 | (variables.gainIndex[4] & 0x1));

				result[variables.resIndex++] = (byte) ((variables.gainIndex[5] & 0x7) << 5 | (variables.gainIndex[6] & 0x7) << 2 | (variables.gainIndex[7] & 0x3));
				result[variables.resIndex++] = (byte) ((variables.gainIndex[8] & 0x7) << 5 | (variables.gainIndex[9] & 0xF) << 1 | (variables.gainIndex[10] & 0x4) >> 2);

				result[variables.resIndex++] = (byte) ((variables.gainIndex[10] & 0x3) << 6 | (variables.gainIndex[11] & 0x7) << 3 | (variables.gainIndex[12] & 0xE) >> 1);
				result[variables.resIndex++] = (byte) ((variables.gainIndex[12] & 0x1) << 7 | (variables.gainIndex[13] & 0x7) << 4 | (variables.gainIndex[14] & 0x7) << 1);
			}
		}

		public static void unpackBits(EncoderBits encoderBits, short[] data, int mode, UnpackBitsVariables variables)
		{
			variables.lsf = encoderBits.LSF;
			variables.cbIndex = encoderBits.cbIndex;
			variables.gainIndex = encoderBits.gainIndex;
			variables.idxVec = encoderBits.idxVec;

			variables.reset();

			/* First WebRtc_Word16 */
			variables.lsf[0] = (short) ((data[variables.tempIndex1] >> 10)
					& 0x3F); /* Bit 0..5 */
			variables.lsf[1] = (short) ((data[variables.tempIndex1] >> 3)
					& 0x7F); /* Bit 6..12 */
			variables.lsf[2] = (short) ((data[variables.tempIndex1]
					& 0x7) << 4); /* Bit 13..15 */

			variables.tempIndex1++;
			/* Second WebRtc_Word16 */
			variables.lsf[2] |= (data[variables.tempIndex1] >> 12)
					& 0xF; /* Bit 0..3 */

			if (mode == 20)
			{
				encoderBits.startIdx = (short) ((data[variables.tempIndex1] >> 10)
						& 0x3); /* Bit 4..5 */

				encoderBits.stateFirst = false;
				if (((data[variables.tempIndex1] >> 9) & 0x1) != 0)
					encoderBits.stateFirst = true; /* Bit 6 */

				encoderBits.idxForMax = (short) ((data[variables.tempIndex1] >> 3)
						& 0x3F); /* Bit 7..12 */
				variables.cbIndex[0] = (short) ((data[variables.tempIndex1]
						& 0x7) << 4); /* Bit 13..15 */
				variables.tempIndex1++;
				/* Third WebRtc_Word16 */
				variables.cbIndex[0] |= (data[variables.tempIndex1] >> 12)
						& 0xE; /* Bit 0..2 */
				variables.gainIndex[0] = (short) ((data[variables.tempIndex1] >> 8)
						& 0x18); /* Bit 3..4 */
				variables.gainIndex[1] = (short) ((data[variables.tempIndex1] >> 7)
						& 0x8); /* Bit 5 */
				variables.cbIndex[3] = (short) ((data[variables.tempIndex1] >> 2)
						& 0xFE); /* Bit 6..12 */
				variables.gainIndex[3] = (short) ((data[variables.tempIndex1] << 2)
						& 0x10); /* Bit 13 */
				variables.gainIndex[4] = (short) ((data[variables.tempIndex1] << 2)
						& 0x8); /* Bit 14 */
				variables.gainIndex[6] = (short) ((data[variables.tempIndex1] << 4)
						& 0x10); /* Bit 15 */
			}
			else
			{ /* mode==30 */
				variables.lsf[3] = (short) ((data[variables.tempIndex1] >> 6)
						& 0x3F); /* Bit 4..9 */
				variables.lsf[4] = (short) ((data[variables.tempIndex1] << 1)
						& 0x7E); /* Bit 10..15 */
				variables.tempIndex1++;
				/* Third WebRtc_Word16 */
				variables.lsf[4] |= (data[variables.tempIndex1] >> 15)
						& 0x1; /* Bit 0 */
				variables.lsf[5] = (short) ((data[variables.tempIndex1] >> 8)
						& 0x7F); /* Bit 1..7 */
				encoderBits.startIdx = (short) ((data[variables.tempIndex1] >> 5)
						& 0x7); /* Bit 8..10 */

				encoderBits.stateFirst = false;
				if ((short) ((data[variables.tempIndex1] >> 4) & 0x1) != 0)
					encoderBits.stateFirst = true;/* Bit 11 */

				variables.tempS = (short) ((data[variables.tempIndex1] << 2)
						& 0x3C);/* Bit 12..15 */
				variables.tempIndex1++;

				/* 4:th WebRtc_Word16 */
				variables.tempS |= (data[variables.tempIndex1] >> 14)
						& 0x3; /* Bit 0..1 */
				encoderBits.idxForMax = variables.tempS;
				variables.cbIndex[0] = (short) ((data[variables.tempIndex1] >> 7)
						& 0x78); /* Bit 2..5 */
				variables.gainIndex[0] = (short) ((data[variables.tempIndex1] >> 5)
						& 0x10); /* Bit 6 */
				variables.gainIndex[1] = (short) ((data[variables.tempIndex1] >> 5)
						& 0x8); /* Bit 7 */
				variables.cbIndex[3] = (short) ((data[variables.tempIndex1])
						& 0xFC); /* Bit 8..13 */
				variables.gainIndex[3] = (short) ((data[variables.tempIndex1] << 3)
						& 0x10); /* Bit 14 */
				variables.gainIndex[4] = (short) ((data[variables.tempIndex1] << 3)
						& 0x8); /* Bit 15 */
			}

			/* Class 2 bits of ULP */
			/*
			 * 4:th to 6:th WebRtc_Word16 for 20 ms case 5:th to 7:th WebRtc_Word16
			 * for 30 ms case
			 */
			variables.tempIndex1++;
			variables.tempIndex2 = 0;

			for (variables.k = 0; variables.k < 3; variables.k++)
			{
				for (variables.i = 15; variables.i >= 0; variables.i--)
					variables.idxVec[variables.tempIndex2++] = (short) (((data[variables.tempIndex1] >> variables.i) << 2)
							& 0x4);/* Bit 15-i */

				variables.tempIndex1++;
			}

			if (mode == 20)
			{
				/* 7:th WebRtc_Word16 */
				for (variables.i = 15; variables.i > 6; variables.i--)
					variables.idxVec[variables.tempIndex2++] = (short) (((data[variables.tempIndex1] >> variables.i) << 2)
							& 0x4); /* Bit 15-i */

				variables.gainIndex[1] |= (data[variables.tempIndex1] >> 4)
						& 0x4; /* Bit 9 */
				variables.gainIndex[3] |= (data[variables.tempIndex1] >> 2)
						& 0xC; /* Bit 10..11 */
				variables.gainIndex[4] |= (data[variables.tempIndex1] >> 1)
						& 0x4; /* Bit 12 */
				variables.gainIndex[6] |= (data[variables.tempIndex1] << 1)
						& 0x8; /* Bit 13 */
				variables.gainIndex[7] = (short) ((data[variables.tempIndex1] << 2)
						& 0xC); /* Bit 14..15 */

			}
			else
			{ /* mode==30 */
				/* 8:th WebRtc_Word16 */
				for (variables.i = 15; variables.i > 5; variables.i--)
					variables.idxVec[variables.tempIndex2++] = (short) (((data[variables.tempIndex1] >> variables.i) << 2)
							& 0x4);/* Bit 15-i */

				variables.cbIndex[0] |= (data[variables.tempIndex1] >> 3)
						& 0x6; /* Bit 10..11 */
				variables.gainIndex[0] |= (data[variables.tempIndex1])
						& 0x8; /* Bit 12 */
				variables.gainIndex[1] |= (data[variables.tempIndex1])
						& 0x4; /* Bit 13 */
				variables.cbIndex[3] |= (data[variables.tempIndex1])
						& 0x2; /* Bit 14 */
				variables.cbIndex[6] = (short) ((data[variables.tempIndex1] << 7)
						& 0x80); /* Bit 15 */
				variables.tempIndex1++;
				/* 9:th WebRtc_Word16 */
				variables.cbIndex[6] |= (data[variables.tempIndex1] >> 9)
						& 0x7E; /* Bit 0..5 */
				variables.cbIndex[9] = (short) ((data[variables.tempIndex1] >> 2)
						& 0xFE); /* Bit 6..12 */
				variables.cbIndex[12] = (short) ((data[variables.tempIndex1] << 5)
						& 0xE0); /* Bit 13..15 */
				variables.tempIndex1++;
				/* 10:th WebRtc_Word16 */
				variables.cbIndex[12] |= (data[variables.tempIndex1] >> 11)
						& 0x1E;/* Bit 0..3 */
				variables.gainIndex[3] |= (data[variables.tempIndex1] >> 8)
						& 0xC; /* Bit 4..5 */
				variables.gainIndex[4] |= (data[variables.tempIndex1] >> 7)
						& 0x6; /* Bit 6..7 */
				variables.gainIndex[6] = (short) ((data[variables.tempIndex1] >> 3)
						& 0x18); /* Bit 8..9 */
				variables.gainIndex[7] = (short) ((data[variables.tempIndex1] >> 2)
						& 0xC); /* Bit 10..11 */
				variables.gainIndex[9] = (short) ((data[variables.tempIndex1] << 1)
						& 0x10); /* Bit 12 */
				variables.gainIndex[10] = (short) ((data[variables.tempIndex1] << 1)
						& 0x8); /* Bit 13 */
				variables.gainIndex[12] = (short) ((data[variables.tempIndex1] << 3)
						& 0x10); /* Bit 14 */
				variables.gainIndex[13] = (short) ((data[variables.tempIndex1] << 3)
						& 0x8); /* Bit 15 */
			}
			variables.tempIndex1++;

			/* Class 3 bits of ULP */
			/*
			 * 8:th to 14:th WebRtc_Word16 for 20 ms case 11:th to 17:th
			 * WebRtc_Word16 for 30 ms case
			 */
			variables.tempIndex2 = 0;
			for (variables.k = 0; variables.k < 7; variables.k++)
			{
				for (variables.i = 14; variables.i >= 0; variables.i -= 2)
					variables.idxVec[variables.tempIndex2++] |= (data[variables.tempIndex1] >> variables.i)
							& 0x3; /* Bit 15-i..14-i */

				variables.tempIndex1++;
			}

			if (mode == 20)
			{
				/* 15:th WebRtc_Word16 */
				variables.idxVec[56] |= (data[variables.tempIndex1] >> 14)
						& 0x3; /* Bit 0..1 */
				variables.cbIndex[0] |= (data[variables.tempIndex1] >> 13)
						& 0x1; /* Bit 2 */
				variables.cbIndex[1] = (short) ((data[variables.tempIndex1] >> 6)
						& 0x7F); /* Bit 3..9 */
				variables.cbIndex[2] = (short) ((data[variables.tempIndex1] << 1)
						& 0x7E); /* Bit 10..15 */
				variables.tempIndex1++;
				/* 16:th WebRtc_Word16 */
				variables.cbIndex[2] |= (data[variables.tempIndex1] >> 15)
						& 0x1; /* Bit 0 */
				variables.gainIndex[0] |= (data[variables.tempIndex1] >> 12)
						& 0x7; /* Bit 1..3 */
				variables.gainIndex[1] |= (data[variables.tempIndex1] >> 10)
						& 0x3; /* Bit 4..5 */
				variables.gainIndex[2] = (short) ((data[variables.tempIndex1] >> 7)
						& 0x7); /* Bit 6..8 */
				variables.cbIndex[3] |= (data[variables.tempIndex1] >> 6)
						& 0x1; /* Bit 9 */
				variables.cbIndex[4] = (short) ((data[variables.tempIndex1] << 1)
						& 0x7E); /* Bit 10..15 */
				variables.tempIndex1++;
				/* 17:th WebRtc_Word16 */
				variables.cbIndex[4] |= (data[variables.tempIndex1] >> 15)
						& 0x1; /* Bit 0 */
				variables.cbIndex[5] = (short) ((data[variables.tempIndex1] >> 8)
						& 0x7F); /* Bit 1..7 */
				variables.cbIndex[6] = (short) ((data[variables.tempIndex1])
						& 0xFF); /* Bit 8..15 */
				variables.tempIndex1++;
				/* 18:th WebRtc_Word16 */
				variables.cbIndex[7] = (short) ((data[variables.tempIndex1] >> 8)
						& 0xFF); /* Bit 0..7 */
				variables.cbIndex[8] = (short) (data[variables.tempIndex1]
						& 0xFF); /* Bit 8..15 */
				variables.tempIndex1++;
				/* 19:th WebRtc_Word16 */
				variables.gainIndex[3] |= (data[variables.tempIndex1] >> 14)
						& 0x3; /* Bit 0..1 */
				variables.gainIndex[4] |= (data[variables.tempIndex1] >> 12)
						& 0x3; /* Bit 2..3 */
				variables.gainIndex[5] = (short) ((data[variables.tempIndex1] >> 9)
						& 0x7); /* Bit 4..6 */
				variables.gainIndex[6] |= (data[variables.tempIndex1] >> 6)
						& 0x7; /* Bit 7..9 */
				variables.gainIndex[7] |= (data[variables.tempIndex1] >> 4)
						& 0x3; /* Bit 10..11 */
				variables.gainIndex[8] = (short) ((data[variables.tempIndex1] >> 1)
						& 0x7); /* Bit 12..14 */
			}
			else
			{ /* mode==30 */
				/* 18:th WebRtc_Word16 */
				variables.idxVec[56] |= (data[variables.tempIndex1] >> 14)
						& 0x3; /* Bit 0..1 */
				variables.idxVec[57] |= (data[variables.tempIndex1] >> 12)
						& 0x3; /* Bit 2..3 */
				variables.cbIndex[0] |= (data[variables.tempIndex1] >> 11)
						& 1; /* Bit 4 */
				variables.cbIndex[1] = (short) ((data[variables.tempIndex1] >> 4)
						& 0x7F); /* Bit 5..11 */
				variables.cbIndex[2] = (short) ((data[variables.tempIndex1] << 3)
						& 0x78); /* Bit 12..15 */
				variables.tempIndex1++;
				/* 19:th WebRtc_Word16 */
				variables.cbIndex[2] |= (data[variables.tempIndex1] >> 13)
						& 0x7; /* Bit 0..2 */
				variables.gainIndex[0] |= (data[variables.tempIndex1] >> 10)
						& 0x7; /* Bit 3..5 */
				variables.gainIndex[1] |= (data[variables.tempIndex1] >> 8)
						& 0x3; /* Bit 6..7 */
				variables.gainIndex[2] = (short) ((data[variables.tempIndex1] >> 5)
						& 0x7); /* Bit 8..10 */
				variables.cbIndex[3] |= (data[variables.tempIndex1] >> 4)
						& 0x1; /* Bit 11 */
				variables.cbIndex[4] = (short) ((data[variables.tempIndex1] << 3)
						& 0x78); /* Bit 12..15 */
				variables.tempIndex1++;
				/* 20:th WebRtc_Word16 */
				variables.cbIndex[4] |= (data[variables.tempIndex1] >> 13)
						& 0x7; /* Bit 0..2 */
				variables.cbIndex[5] = (short) ((data[variables.tempIndex1] >> 6)
						& 0x7F); /* Bit 3..9 */
				variables.cbIndex[6] |= (data[variables.tempIndex1] >> 5)
						& 0x1; /* Bit 10 */
				variables.cbIndex[7] = (short) ((data[variables.tempIndex1] << 3)
						& 0xF8); /* Bit 11..15 */
				variables.tempIndex1++;
				/* 21:st WebRtc_Word16 */
				variables.cbIndex[7] |= (data[variables.tempIndex1] >> 13)
						& 0x7; /* Bit 0..2 */
				variables.cbIndex[8] = (short) ((data[variables.tempIndex1] >> 5)
						& 0xFF); /* Bit 3..10 */
				variables.cbIndex[9] |= (data[variables.tempIndex1] >> 4)
						& 0x1; /* Bit 11 */
				variables.cbIndex[10] = (short) ((data[variables.tempIndex1] << 4)
						& 0xF0); /* Bit 12..15 */
				variables.tempIndex1++;
				/* 22:nd WebRtc_Word16 */
				variables.cbIndex[10] |= (data[variables.tempIndex1] >> 12)
						& 0xF; /* Bit 0..3 */
				variables.cbIndex[11] = (short) ((data[variables.tempIndex1] >> 4)
						& 0xFF); /* Bit 4..11 */
				variables.cbIndex[12] |= (data[variables.tempIndex1] >> 3)
						& 0x1; /* Bit 12 */
				variables.cbIndex[13] = (short) ((data[variables.tempIndex1] << 5)
						& 0xE0); /* Bit 13..15 */
				variables.tempIndex1++;
				/* 23:rd WebRtc_Word16 */
				variables.cbIndex[13] |= (data[variables.tempIndex1] >> 11)
						& 0x1F;/* Bit 0..4 */
				variables.cbIndex[14] = (short) ((data[variables.tempIndex1] >> 3)
						& 0xFF); /* Bit 5..12 */
				variables.gainIndex[3] |= (data[variables.tempIndex1] >> 1)
						& 0x3; /* Bit 13..14 */
				variables.gainIndex[4] |= (data[variables.tempIndex1]
						& 0x1); /* Bit 15 */
				variables.tempIndex1++;
				/* 24:rd WebRtc_Word16 */
				variables.gainIndex[5] = (short) ((data[variables.tempIndex1] >> 13)
						& 0x7); /* Bit 0..2 */
				variables.gainIndex[6] |= (data[variables.tempIndex1] >> 10)
						& 0x7; /* Bit 3..5 */
				variables.gainIndex[7] |= (data[variables.tempIndex1] >> 8)
						& 0x3; /* Bit 6..7 */
				variables.gainIndex[8] = (short) ((data[variables.tempIndex1] >> 5)
						& 0x7); /* Bit 8..10 */
				variables.gainIndex[9] |= (data[variables.tempIndex1] >> 1)
						& 0xF; /* Bit 11..14 */
				variables.gainIndex[10] |= (data[variables.tempIndex1] << 2)
						& 0x4; /* Bit 15 */
				variables.tempIndex1++;
				/* 25:rd WebRtc_Word16 */
				variables.gainIndex[10] |= (data[variables.tempIndex1] >> 14)
						& 0x3; /* Bit 0..1 */
				variables.gainIndex[11] = (short) ((data[variables.tempIndex1] >> 11)
						& 0x7); /* Bit 2..4 */
				variables.gainIndex[12] |= (data[variables.tempIndex1] >> 7)
						& 0xF; /* Bit 5..8 */
				variables.gainIndex[13] |= (data[variables.tempIndex1] >> 4)
						& 0x7; /* Bit 9..11 */
				variables.gainIndex[14] = (short) ((data[variables.tempIndex1] >> 1)
						& 0x7); /* Bit 12..14 */
			}
		}

		public static void updateDecIndex(EncoderBits encoderBits, UpdateDecIndexVariables variables)
		{
			variables.index = encoderBits.cbIndex;

			for (variables.k = 4; variables.k < 6; variables.k++)
			{
				if (variables.index[variables.k] >= 44 && variables.index[variables.k] < 108)
					variables.index[variables.k] += 64;
				else if (variables.index[variables.k] >= 108 && variables.index[variables.k] < 128)
					variables.index[variables.k] += 128;
			}
		}

		public static void simpleLsfDeq(short[] lsfDeq, int lsfDeqIndex, short[] index, int indexIndex, int lpcN, SimpleLsfDeqVariables variables)
		{
			variables.reset();

			for (variables.i = 0; variables.i < 3; variables.i++)
			{
				variables.cbIndex = ILBC.LSF_INDEX_CB[variables.i];
				for (variables.j = 0; variables.j < ILBC.LSF_DIM_CB[variables.i]; variables.j++)
					lsfDeq[lsfDeqIndex++] = ILBC.LSF_CB[variables.cbIndex + index[indexIndex] * ILBC.LSF_DIM_CB[variables.i] + variables.j];

				indexIndex++;
			}

			if (lpcN > 1)
			{
				/* decode last LSF */
				for (variables.i = 0; variables.i < 3; variables.i++)
				{
					variables.cbIndex = ILBC.LSF_INDEX_CB[variables.i];
					for (variables.j = 0; variables.j < ILBC.LSF_DIM_CB[variables.i]; variables.j++)
						lsfDeq[lsfDeqIndex++] = ILBC.LSF_CB[variables.cbIndex + index[indexIndex] * ILBC.LSF_DIM_CB[variables.i] + variables.j];

					indexIndex++;
				}
			}
		}

		public static void decoderInterpolateLsf(DecoderState decoderState, short[] syntDenum, int syntDenumIndex, short[] weightDenum, int weightDenumIndex, short[] lsfDeq, int lsfDeqIndex, short length, DecodeInterpolateLsfVariables variables,Mode mode)
		{
			variables.reset();
			variables.len = length + 1;

			if (mode == Mode.MODE_30)
			{
				lspInterpolate2PolyDec(variables.lp, 0, decoderState.lsfDeqOld, 0, lsfDeq, lsfDeqIndex, ILBC.LSF_WEIGHT_30MS[0], length, variables.lspInterpolate2PolyDecVariables);
				System.arraycopy(variables.lp, 0, syntDenum, syntDenumIndex, variables.len);
				BasicFunctions.expand(weightDenum, weightDenumIndex, variables.lp, 0, ILBC.LPC_CHIRP_SYNT_DENUM, variables.len);

				for (variables.s = 1; variables.s < mode.subframes; variables.s++)
				{
					syntDenumIndex += variables.len;
					weightDenumIndex += variables.len;
					lspInterpolate2PolyDec(variables.lp, 0, lsfDeq, lsfDeqIndex, lsfDeq, lsfDeqIndex + length, ILBC.LSF_WEIGHT_30MS[variables.s], length, variables.lspInterpolate2PolyDecVariables);
					System.arraycopy(variables.lp, 0, syntDenum, syntDenumIndex, variables.len);
					BasicFunctions.expand(weightDenum, weightDenumIndex, variables.lp, 0, ILBC.LPC_CHIRP_SYNT_DENUM, variables.len);
				}
			}
			else
			{
				for (variables.s = 0; variables.s < mode.subframes; variables.s++)
				{
					lspInterpolate2PolyDec(variables.lp, 0, decoderState.lsfDeqOld, 0, lsfDeq, lsfDeqIndex, ILBC.LSF_WEIGHT_20MS[variables.s], length, variables.lspInterpolate2PolyDecVariables);
					System.arraycopy(variables.lp, 0, syntDenum, syntDenumIndex, variables.len);
					BasicFunctions.expand(weightDenum, weightDenumIndex, variables.lp, 0, ILBC.LPC_CHIRP_SYNT_DENUM, variables.len);
					syntDenumIndex += variables.len;
					weightDenumIndex += variables.len;
				}
			}

			if (mode == Mode.MODE_30)
				System.arraycopy(lsfDeq, lsfDeqIndex + length, decoderState.lsfDeqOld, 0, length);
			else System.arraycopy(lsfDeq, lsfDeqIndex, decoderState.lsfDeqOld, 0, length);
		}

		public static void lspInterpolate2PolyDec(short[] a, int aIndex, short[] lsf1, int lsf1Index, short[] lsf2, int lsf2Index, short coef, int length, LspInterpolate2PolyDecVariables variables)
		{
			variables.reset();
			interpolate(variables.lsfTemp, 0, lsf1, lsf1Index, lsf2, lsf2Index, coef, length, variables.interpolateVariables);
			lsf2Poly(a, aIndex, variables.lsfTemp, 0, variables.lsf2PolyVariables);
		}

		public static void decodeResidual(DecoderState decoderState, EncoderBits encoderBits, short[] decResidual, int decResidualIndex, short[] syntDenum, int syntDenumIndex, DecodeResidualVariables variables,Mode mode)
		{
			variables.reverseDecresidual = decoderState.enhancementBuffer;
			variables.memVec = decoderState.prevResidual;

			variables.diff = (short) (80 - mode.state_short_len);

			if (encoderBits.stateFirst)
				variables.startPos = (short) ((encoderBits.startIdx - 1) * 40);
			else variables.startPos = (short) ((encoderBits.startIdx - 1) * 40 + variables.diff);

			stateConstruct(encoderBits, syntDenum, syntDenumIndex + (encoderBits.startIdx - 1) * 11, decResidual, decResidualIndex + variables.startPos, mode.state_short_len, variables.stateConstructVariables);

			if (encoderBits.stateFirst)
			{
				for (variables.i = 4; variables.i < 151 - mode.state_short_len; variables.i++)
					variables.memVec[variables.i] = 0;

				System.arraycopy(decResidual, variables.startPos, variables.memVec, 151 - mode.state_short_len, mode.state_short_len);
				cbConstruct(encoderBits, decResidual, decResidualIndex + variables.startPos + mode.state_short_len, variables.memVec, 66, (short) 85, variables.diff, 0, 0, variables.cbConstructVariables);
			}
			else
			{
				variables.memlGotten = mode.state_short_len;
				BasicFunctions.reverseCopy(variables.memVec, 150, decResidual, decResidualIndex + variables.startPos, variables.memlGotten);

				for (variables.i = 4; variables.i < 151 - variables.memlGotten; variables.i++)
					variables.memVec[variables.i] = 0;

				cbConstruct(encoderBits, variables.reverseDecresidual, 0, variables.memVec, 66, (short) 85, variables.diff, 0, 0, variables.cbConstructVariables);
				BasicFunctions.reverseCopy(decResidual, decResidualIndex + variables.startPos - 1, variables.reverseDecresidual, 0, variables.diff);
			}

			variables.subCount = 1;
			variables.nFor = (short) (mode.subframes - encoderBits.startIdx - 1);

			if (variables.nFor > 0)
			{
				for (variables.i = 4; variables.i < 71; variables.i++)
					variables.memVec[variables.i] = 0;

				System.arraycopy(decResidual, decResidualIndex + 40 * (encoderBits.startIdx - 1), variables.memVec, 71, 80);

				for (variables.subFrame = 0; variables.subFrame < variables.nFor; variables.subFrame++)
				{
					cbConstruct(encoderBits, decResidual, decResidualIndex + 40 * (encoderBits.startIdx + 1 + variables.subFrame), variables.memVec, 4, (short) 147, (short) 40, variables.subCount * 3, variables.subCount * 3, variables.cbConstructVariables);

					for (variables.i = 4; variables.i < 111; variables.i++)
						variables.memVec[variables.i] = variables.memVec[variables.i + 40];

					System.arraycopy(decResidual, decResidualIndex + 40 * (encoderBits.startIdx + 1 + variables.subFrame), variables.memVec, 111, 40);
					variables.subCount++;
				}
			}

			variables.nBack = (short) (encoderBits.startIdx - 1);

			if (variables.nBack > 0)
			{
				variables.memlGotten = (short) (40 * (mode.subframes + 1 - encoderBits.startIdx));
				if (variables.memlGotten > 147)
					variables.memlGotten = 147;

				BasicFunctions.reverseCopy(variables.memVec, 150, decResidual, decResidualIndex + 40 * (encoderBits.startIdx - 1), variables.memlGotten);

				for (variables.i = 4; variables.i < 151 - variables.memlGotten; variables.i++)
					variables.memVec[variables.i] = 0;

				for (variables.subFrame = 0; variables.subFrame < variables.nBack; variables.subFrame++)
				{
					cbConstruct(encoderBits, variables.reverseDecresidual, 40 * variables.subFrame, variables.memVec, 4, (short) 147, (short) 40, variables.subCount * 3, variables.subCount * 3, variables.cbConstructVariables);

					for (variables.i = 4; variables.i < 111; variables.i++)
						variables.memVec[variables.i] = variables.memVec[variables.i + 40];

					System.arraycopy(variables.reverseDecresidual, 40 * variables.subFrame, variables.memVec, 111, 40);
					variables.subCount++;
				}

				BasicFunctions.reverseCopy(decResidual, decResidualIndex + 40 * variables.nBack - 1, variables.reverseDecresidual, 0, 40 * variables.nBack);
			}
		}

		public static int xCorrCoef(short[] target, int targetIndex, short[] regressor, int regressorIndex, short subl, short searchLen, short offset, short step, XCorrCoefVariables variables)
		{
			variables.energyModMax = Short.MAX_VALUE;
			variables.totScaleMax = -500;
			variables.crossCorrSqModMax = 0;
			variables.maxLag = 0;

			variables.pos = 0;

			if (step == 1)
			{
				variables.max = BasicFunctions.getMaxAbsValue(regressor, regressorIndex, subl + searchLen - 1);
				variables.tempIndex1 = regressorIndex;
				variables.tempIndex2 = regressorIndex + subl;
			}
			else
			{
				variables.max = BasicFunctions.getMaxAbsValue(regressor, regressorIndex - searchLen, subl + searchLen - 1);
				variables.tempIndex1 = regressorIndex - 1;
				variables.tempIndex2 = regressorIndex + subl - 1;
			}

			if (variables.max > 5000)
				variables.shifts = 2;
			else variables.shifts = 0;

			variables.energy = BasicFunctions.scaleRight(regressor, regressorIndex, regressor, regressorIndex, subl, variables.shifts);
			for (variables.k = 0; variables.k < searchLen; variables.k++)
			{
				variables.tempIndex3 = targetIndex;
				variables.tempIndex4 = regressorIndex + variables.pos;

				variables.crossCorr = BasicFunctions.scaleRight(target, variables.tempIndex3, regressor, variables.tempIndex4, subl, variables.shifts);
				if ((variables.energy > 0) && (variables.crossCorr > 0))
				{
					/* Put cross correlation and energy on 16 bit word */
					variables.crossCorrScale = (short) (BasicFunctions.norm(variables.crossCorr) - 16);

					if (variables.crossCorrScale > 0)
						variables.crossCorrMod = (short) (variables.crossCorr << variables.crossCorrScale);
					else variables.crossCorrMod = (short) (variables.crossCorr >> (0 - variables.crossCorrScale));

					variables.energyScale = (short) (BasicFunctions.norm(variables.energy) - 16);

					if (variables.energyScale > 0)
						variables.energyMod = (short) (variables.energy << variables.energyScale);
					else variables.energyMod = (short) (variables.energy >> (0 - variables.energyScale));

					/* Square cross correlation and store upper WebRtc_Word16 */
					variables.crossCorrSqMod = (short) ((variables.crossCorrMod * variables.crossCorrMod) >> 16);

					/*
					 * Calculate the total number of (dynamic) right shifts that
					 * have been performed on (crossCorr*crossCorr)/energy
					 */
					variables.totScale = (short) (variables.energyScale - (variables.crossCorrScale << 1));

					/*
					 * Calculate the shift difference in order to be able to compare
					 * the two (crossCorr*crossCorr)/energy in the same domain
					 */
					variables.scaleDiff = (short) (variables.totScale - variables.totScaleMax);
					if (variables.scaleDiff > 31)
						variables.scaleDiff = 31;
					else if (variables.scaleDiff < -31)
						variables.scaleDiff = -31;

					/*
					 * Compute the cross multiplication between the old best
					 * criteria and the new one to be able to compare them without
					 * using a division
					 */

					if (variables.scaleDiff < 0)
					{
						variables.newCrit = ((variables.crossCorrSqMod * variables.energyModMax) >> (-variables.scaleDiff));
						variables.maxCrit = variables.crossCorrSqModMax * variables.energyMod;
					}
					else
					{
						variables.newCrit = variables.crossCorrSqMod * variables.energyModMax;
						variables.maxCrit = ((variables.crossCorrSqModMax * variables.energyMod) >> variables.scaleDiff);
					}

					/*
					 * Store the new lag value if the new criteria is larger than
					 * previous largest criteria
					 */

					if (variables.newCrit > variables.maxCrit)
					{
						variables.crossCorrSqModMax = variables.crossCorrSqMod;
						variables.energyModMax = variables.energyMod;
						variables.totScaleMax = variables.totScale;
						variables.maxLag = (short) variables.k;
					}
				}

				variables.pos += step;

				/* Do a +/- to get the next energy */
				variables.temp = regressor[variables.tempIndex2] * regressor[variables.tempIndex2] - regressor[variables.tempIndex1] * regressor[variables.tempIndex1];
				variables.temp >>= variables.shifts;
				variables.energy += step * variables.temp;

				variables.tempIndex1 += step;
				variables.tempIndex2 += step;
			}

			return (variables.maxLag + offset);
		}

		public static void doThePlc(DecoderState decoderState, short[] plcResidual, int plcResidualIndex, short[] plcLpc, int plcLpcIndex, short pli, short[] decResidual, int decResidualIndex, short[] lpc, int lpcIndex, short inLag, DoThePlcVariables variables,Mode mode)
		{
			variables.tempCorrData.energy = 0;

			if (pli == 1)
			{
				decoderState.consPliCount = decoderState.consPliCount + 1;
				if (decoderState.prevPli != 1)
				{
					variables.max = BasicFunctions.getMaxAbsValue(decoderState.prevResidual, 0, mode.size);
					variables.scale = (short) ((BasicFunctions.getSize(variables.max) << 1) - 25);
					if (variables.scale < 0)
						variables.scale = 0;

					decoderState.prevScale = variables.scale;
					variables.lag = (short) (inLag - 3);

					if (60 > mode.size - inLag - 3)
						variables.corrLen = 60;
					else variables.corrLen = (short) (mode.size - inLag - 3);

					compCorr(variables.corrData, decoderState.prevResidual, 0, variables.lag, mode.size, variables.corrLen, variables.scale);

					variables.shiftMax = (short) (BasicFunctions.getSize(Math.abs(variables.corrData.correlation)) - 15);
					if (variables.shiftMax > 0)
					{
						variables.tempShift = variables.corrData.correlation >> variables.shiftMax;
						variables.tempShift = variables.tempShift * variables.tempShift;
						variables.crossSquareMax = (short) (variables.tempShift >> 15);
					}
					else
					{
						variables.tempShift = variables.corrData.correlation << (0 - variables.shiftMax);
						variables.tempShift = variables.tempShift * variables.tempShift;
						variables.crossSquareMax = (short) (variables.tempShift >> 15);
					}

					for (variables.j = inLag - 2; variables.j <= inLag + 3; variables.j++)
					{
						compCorr(variables.tempCorrData, decoderState.prevResidual, 0, (short) variables.j, mode.size, variables.corrLen, variables.scale);

						variables.shift1 = (short) (BasicFunctions.getSize(Math.abs(variables.tempCorrData.correlation) - 15));
						if (variables.shift1 > 0)
						{
							variables.tempShift = variables.tempCorrData.correlation >> variables.shift1;
							variables.tempShift = variables.tempShift * variables.tempShift;
							variables.crossSquare = (short) (variables.tempShift >> 15);
						}
						else
						{
							variables.tempShift = variables.tempCorrData.correlation << (0 - variables.shift1);
							variables.tempShift = variables.tempShift * variables.tempShift;
							variables.crossSquare = (short) (variables.tempShift >> 15);
						}

						variables.shift2 = (short) (BasicFunctions.getSize(variables.corrData.energy) - 15);
						if (variables.shift2 > 0)
							variables.measure = (variables.corrData.energy >> variables.shift2) * variables.crossSquare;
						else variables.measure = (variables.corrData.energy << (0 - variables.shift2)) * variables.crossSquare;

						variables.shift3 = (short) (BasicFunctions.getSize(variables.tempCorrData.energy) - 15);
						if (variables.shift3 > 0)
							variables.maxMeasure = (variables.tempCorrData.energy >> variables.shift3) * variables.crossSquareMax;
						else variables.maxMeasure = (variables.tempCorrData.energy << (0 - variables.shift3)) * variables.crossSquareMax;

						if (((variables.shiftMax << 1) + variables.shift3) > ((variables.shift1 << 1) + variables.shift2))
						{
							variables.tempShift = (variables.shiftMax << 1);
							variables.tempShift -= (variables.shift1 << 1);
							variables.tempShift = variables.tempShift + variables.shift3 - variables.shift2;
							if (variables.tempShift > 31)
								variables.tempS = 31;
							else variables.tempS = (short) variables.tempShift;

							variables.tempS2 = 0;
						}
						else
						{
							variables.tempS = 0;
							variables.tempShift = (variables.shift1 << 1);
							variables.tempShift -= (variables.shiftMax << 1);
							variables.tempShift = variables.tempShift + variables.shift2 - variables.shift3;
							if (variables.tempShift > 31)
								variables.tempS2 = 31;
							else variables.tempS2 = (short) variables.tempShift;
						}

						if ((variables.measure >> variables.tempS) > (variables.maxMeasure >> variables.tempS2))
						{
							variables.lag = (short) variables.j;
							variables.crossSquareMax = variables.crossSquare;
							variables.corrData.correlation = variables.tempCorrData.correlation;
							variables.shiftMax = variables.shift1;
							variables.corrData.energy = variables.tempCorrData.energy;
						}
					}

					variables.temp2 = BasicFunctions.scaleRight(decoderState.prevResidual, mode.size - variables.corrLen, decoderState.prevResidual, mode.size - variables.corrLen, variables.corrLen, variables.scale);

					if ((variables.temp2 > 0) && (variables.tempCorrData.energy > 0))
					{
						variables.scale1 = (short) (BasicFunctions.norm(variables.temp2) - 16);
						if (variables.scale1 > 0)
							variables.tempS = (short) (variables.temp2 << variables.scale1);
						else variables.tempS = (short) (variables.temp2 >> (0 - variables.scale1));

						variables.scale2 = (short) (BasicFunctions.norm(variables.corrData.energy) - 16);
						if (variables.scale2 > 0)
							variables.tempS2 = (short) (variables.corrData.energy << variables.scale2);
						else variables.tempS2 = (short) (variables.corrData.energy >> (0 - variables.scale2));

						variables.denom = (short) ((variables.tempS * variables.tempS2) >> 16);

						variables.totScale = (short) (variables.scale1 + variables.scale2 - 1);
						variables.tempShift = (variables.totScale >> 1);
						if (variables.tempShift > 0)
							variables.tempS = (short) (variables.corrData.correlation << variables.tempShift);
						else variables.tempS = (short) (variables.corrData.correlation >> (0 - variables.tempShift));

						variables.tempShift = variables.totScale - variables.tempShift;
						if (variables.tempShift > 0)
							variables.tempS2 = (short) (variables.corrData.correlation << variables.tempShift);
						else variables.tempS2 = (short) (variables.corrData.correlation >> (0 - variables.tempShift));

						variables.nom = (short) (variables.tempS * variables.tempS2);
						variables.maxPerSquare = (short) (variables.nom / variables.denom);

					}
					else variables.maxPerSquare = 0;
				}
				else
				{
					variables.lag = decoderState.prevLag;
					variables.maxPerSquare = decoderState.perSquare;
				}

				variables.useGain = 32767;

				if (decoderState.consPliCount * mode.size > 320)
					variables.useGain = 29491;
				else if (decoderState.consPliCount * mode.size > 640)
					variables.useGain = 22938;
				else if (decoderState.consPliCount * mode.size > 960)
					variables.useGain = 16384;
				else if (decoderState.consPliCount * mode.size > 1280)
					variables.useGain = 0;

				if (variables.maxPerSquare > 7868)
					variables.pitchFact = 32767;
				else if (variables.maxPerSquare > 839)
				{
					variables.ind = 5;
					while ((variables.maxPerSquare < ILBC.PLC_PER_SQR[variables.ind]) && (variables.ind > 0))
						variables.ind--;

					variables.temp = ILBC.PLC_PITCH_FACT[variables.ind];
					variables.temp += ((ILBC.PLC_PF_SLOPE[variables.ind] * (variables.maxPerSquare - ILBC.PLC_PER_SQR[variables.ind])) >> 11);

					if (variables.temp > Short.MIN_VALUE)
						variables.pitchFact = Short.MIN_VALUE;
					else variables.pitchFact = (short) variables.temp;
				}
				else variables.pitchFact = 0;

				variables.useLag = variables.lag;
				if (variables.lag < 80)
					variables.useLag = (short) (2 * variables.lag);

				variables.energy = 0;
				for (variables.i = 0; variables.i < mode.size; variables.i++)
				{
					decoderState.seed = (short) ((decoderState.seed * 31821) + 13849);
					variables.randLag = (short) (53 + (decoderState.seed & 63));

					variables.pick = (short) (variables.i - variables.randLag);
					if (variables.pick < 0)
						variables.randVec[variables.i] = decoderState.prevResidual[mode.size + variables.pick];
					else variables.randVec[variables.i] = decoderState.prevResidual[variables.pick];

					variables.pick = (short) (variables.i - variables.useLag);

					if (variables.pick < 0)
						plcResidual[plcResidualIndex + variables.i] = decoderState.prevResidual[mode.size + variables.pick];
					else plcResidual[plcResidualIndex + variables.i] = plcResidual[plcResidualIndex + variables.pick];

					if (variables.i < 80)
						variables.totGain = variables.useGain;
					else if (variables.i < 160)
						variables.totGain = (short) ((31130 * variables.useGain) >> 15);
					else variables.totGain = (short) ((29491 * variables.useGain) >> 15);

					variables.tempShift = variables.pitchFact * plcResidual[plcResidualIndex + variables.i];
					variables.tempShift += (32767 - variables.pitchFact) * variables.randVec[variables.i];
					variables.tempShift += 16384;
					variables.temp = (short) (variables.tempShift >> 15);
					plcResidual[plcResidualIndex + variables.i] = (short) ((variables.totGain * variables.temp) >> 15);

					variables.tempShift = plcResidual[plcResidualIndex + variables.i] * plcResidual[plcResidualIndex + variables.i];
					variables.energy += (short) (variables.tempShift >> (decoderState.prevScale + 1));
				}

				variables.tempShift = mode.size * 900;
				if (decoderState.prevScale + 1 > 0)
					variables.tempShift = variables.tempShift >> (decoderState.prevScale + 1);
				else variables.tempShift = variables.tempShift << (0 - decoderState.prevScale - 1);

				if (variables.energy < variables.tempShift)
				{
					variables.energy = 0;
					for (variables.i = 0; variables.i < mode.size; variables.i++)
						plcResidual[plcResidualIndex + variables.i] = variables.randVec[variables.i];
				}

				System.arraycopy(decoderState.prevLpc, 0, plcLpc, plcLpcIndex, 10);
				decoderState.prevLag = variables.lag;
				decoderState.perSquare = variables.maxPerSquare;
			}
			else
			{
				System.arraycopy(decResidual, decResidualIndex, plcResidual, plcResidualIndex, mode.size);
				System.arraycopy(lpc, lpcIndex, plcLpc, plcLpcIndex, 11);
				decoderState.consPliCount = 0;
			}

			decoderState.prevPli = pli;
			System.arraycopy(plcLpc, plcLpcIndex, decoderState.prevLpc, 0, 11);
			System.arraycopy(plcResidual, plcResidualIndex, decoderState.prevResidual, 0, mode.size);
		}

		public static void compCorr(CorrData currData, short[] buffer, int bufferIndex, short lag, short bLen, short sRange, short scale)
		{
			int currIndex = bLen - sRange - lag;

			if (scale > 0)
			{
				currData.correlation = BasicFunctions.scaleRight(buffer, bufferIndex + bLen - sRange, buffer, currIndex, sRange, scale);
				currData.energy = BasicFunctions.scaleRight(buffer, currIndex, buffer, currIndex, sRange, scale);
			}
			else
			{
				currData.correlation = BasicFunctions.scaleLeft(buffer, bufferIndex + bLen - sRange, buffer, currIndex, sRange, (0 - scale));
				currData.energy = BasicFunctions.scaleLeft(buffer, currIndex, buffer, currIndex, sRange, scale);
			}

			if (currData.correlation == 0)
			{
				currData.correlation = 0;
				currData.energy = 1;
			}
		}

		public static int enchancher(short[] in, int inIndex, short[] out, int outIndex, DecoderState decoderState, EnhancerVariables variables,Mode mode)
		{
			variables.reset();
			variables.lag = 20;
			variables.tLag = 20;
			variables.inputLength = mode.size + 120;

			variables.enhancementBuffer = decoderState.enhancementBuffer;
			variables.enhancementPeriod = decoderState.enhancementPeriod;

			System.arraycopy(variables.enhancementBuffer, mode.size, variables.enhancementBuffer, 0, variables.enhancementBuffer.length - mode.size);
			System.arraycopy(in, inIndex, variables.enhancementBuffer, 640 - mode.size, mode.size);

			if (mode == Mode.MODE_30)
			{
				variables.plcBlock = 80;
				variables.newBlocks = 3;
				variables.startPos = 320;
			}
			else
			{
				variables.plcBlock = 40;
				variables.newBlocks = 2;
				variables.startPos = 440;
			}

			System.arraycopy(variables.enhancementPeriod, variables.newBlocks, variables.enhancementPeriod, 0, variables.enhancementPeriod.length - variables.newBlocks);
			BasicFunctions.downsampleFast(variables.enhancementBuffer, 640 - variables.inputLength, variables.inputLength + 3, variables.downsampled, 0, variables.inputLength >> 1, ILBC.LP_FILT_COEFS, 7, 2, 3);

			for (variables.iBlock = 0; variables.iBlock < variables.newBlocks; variables.iBlock++)
			{
				variables.targetIndex = 60 + variables.iBlock * 40;
				variables.regressorIndex = variables.targetIndex - 10;
				variables.max16 = BasicFunctions.getMaxAbsValue(variables.downsampled, variables.regressorIndex - 50, 89);
				variables.shifts = (short) (BasicFunctions.getSize(variables.max16 * variables.max16) - 25);
				if (variables.shifts < 0)
					variables.shifts = 0;

				crossCorrelation(variables.corr32, 0, variables.downsampled, variables.targetIndex, variables.downsampled, variables.regressorIndex, (short) 40, (short) 50, variables.shifts, (short) -1, variables.crossCorrelationVariables);

				for (variables.i = 0; variables.i < 2; variables.i++)
				{
					variables.lagMax[variables.i] = (short) BasicFunctions.getMaxIndex(variables.corr32, 0, 50);
					variables.corrMax[variables.i] = variables.corr32[variables.lagMax[variables.i]];
					variables.start = variables.lagMax[variables.i] - 2;
					variables.stop = variables.lagMax[variables.i] + 2;
					if (variables.start < 0)
						variables.start = 0;

					if (variables.stop > 49)
						variables.stop = 49;

					System.arraycopy(ILBC.emptyIntArray, 0, variables.corr32, variables.start, variables.stop - variables.start);
				}

				variables.lagMax[2] = (short) BasicFunctions.getMaxIndex(variables.corr32, 0, 50);
				variables.corrMax[2] = variables.corr32[variables.lagMax[2]];

				for (variables.i = 0; variables.i < 3; variables.i++)
				{
					variables.corrSh = 15 - BasicFunctions.getSize(variables.corrMax[variables.i]);
					variables.ener = BasicFunctions.scaleRight(variables.downsampled, variables.regressorIndex - variables.lagMax[variables.i], variables.downsampled, variables.regressorIndex - variables.lagMax[variables.i], 40, variables.shifts);
					variables.enerSh = 15 - BasicFunctions.getSize(variables.ener);

					if (variables.corrSh > 0)
						variables.corr16[variables.i] = (short) (variables.corrMax[variables.i] >> variables.corrSh);
					else variables.corr16[variables.i] = (short) (variables.corrMax[variables.i] << variables.corrSh);

					variables.corr16[variables.i] = (short) ((variables.corr16[variables.i] * variables.corr16[variables.i]) >> 16);

					if (variables.enerSh > 0)
						variables.en16[variables.i] = (short) (variables.ener >> variables.enerSh);
					else variables.en16[variables.i] = (short) (variables.ener << variables.enerSh);

					variables.totSh[variables.i] = (short) (variables.enerSh - (variables.corrSh << 1));
				}

				variables.index = 0;
				for (variables.i = 1; variables.i < 3; variables.i++)
				{
					if (variables.totSh[variables.index] > variables.totSh[variables.i])
					{
						variables.sh = (short) (variables.totSh[variables.index] - variables.totSh[variables.i]);
						if (variables.sh > 31)
							variables.sh = 31;

						if (variables.corr16[variables.index] * variables.en16[variables.i] < ((variables.corr16[variables.i] * variables.en16[variables.index]) >> variables.sh))
							variables.index = variables.i;
					}
					else
					{
						variables.sh = (short) (variables.totSh[variables.i] - variables.totSh[variables.index]);
						if (variables.sh > 31)
							variables.sh = 31;

						if (((variables.corr16[variables.index] * variables.en16[variables.i]) >> variables.sh) < variables.corr16[variables.i] * variables.en16[variables.index])
							variables.index = variables.i;
					}
				}

				variables.lag = variables.lagMax[variables.index] + 10;

				variables.enhancementPeriod[8 - variables.newBlocks + variables.iBlock] = (short) (variables.lag * 8);

				if (decoderState.prevEnchPl == 1)
				{
					if (variables.iBlock == 0)
						variables.tLag = variables.lag * 2;
				}
				else if (variables.iBlock == 1)
					variables.tLag = variables.lag * 2;

				variables.lag = variables.lag * 2;
			}

			if (decoderState.prevEnchPl == 1 || decoderState.prevEnchPl == 2)
			{
				variables.targetIndex = inIndex;
				variables.regressorIndex = inIndex + variables.tLag - 1;

				variables.max16 = BasicFunctions.getMaxAbsValue(in, inIndex, variables.plcBlock + 2);
				if (variables.max16 > 5000)
					variables.shifts = 2;
				else variables.shifts = 0;

				crossCorrelation(variables.corr32, 0, in, variables.targetIndex, in, variables.regressorIndex, (short) variables.plcBlock, (short) 3, (short) variables.shifts, (short) 1, variables.crossCorrelationVariables);
				variables.lag = BasicFunctions.getMaxIndex(variables.corr32, 0, 3);
				variables.lag += variables.tLag - 1;

				if (decoderState.prevEnchPl == 1)
				{
					if (variables.lag > variables.plcBlock)
						System.arraycopy(in, inIndex + variables.lag - variables.plcBlock, variables.downsampled, 0, variables.plcBlock);
					else
					{
						System.arraycopy(in, inIndex, variables.downsampled, variables.plcBlock - variables.lag, variables.lag);
						System.arraycopy(variables.enhancementBuffer, 640 - mode.size - variables.plcBlock + variables.lag, variables.downsampled, 0, variables.plcBlock - variables.lag);
					}
				}
				else
				{
					variables.pos = variables.plcBlock;

					while (variables.lag < variables.pos)
					{
						System.arraycopy(in, inIndex, variables.downsampled, variables.pos - variables.lag, variables.lag);
						variables.pos = variables.pos - variables.lag;
					}

					System.arraycopy(in, inIndex + variables.lag - variables.pos, variables.downsampled, 0, variables.pos);
				}

				if (decoderState.prevEnchPl == 1)
				{
					variables.max = BasicFunctions.getMaxAbsValue(variables.enhancementBuffer, 640 - mode.size - variables.plcBlock, variables.plcBlock);
					variables.max16 = BasicFunctions.getMaxAbsValue(variables.downsampled, 0, variables.plcBlock);
					if (variables.max16 > variables.max)
						variables.max = variables.max16;

					variables.scale = (short) (22 - BasicFunctions.norm(variables.max));
					if (variables.scale < 0)
						variables.scale = 0;

					variables.temp2 = BasicFunctions.scaleRight(variables.enhancementBuffer, 640 - mode.size - variables.plcBlock, variables.enhancementBuffer, 640 - mode.size - variables.plcBlock, variables.plcBlock, variables.scale);
					variables.temp1 = BasicFunctions.scaleRight(variables.downsampled, 0, variables.downsampled, 0, variables.plcBlock, variables.scale);

					if ((variables.temp1 > 0) && ((variables.temp1 >> 2) > variables.temp2))
					{
						variables.scale1 = (short) BasicFunctions.norm(variables.temp1);
						if (variables.scale1 > 16)
							variables.temp1 = (variables.temp1 >> (variables.scale1 - 16));
						else variables.temp1 = (variables.temp1 << (variables.scale1 - 16));

						variables.temp2 = (variables.temp2 >> variables.scale1);
						variables.sqrtEnChange = (short) BasicFunctions.sqrtFloor((variables.temp2 / variables.temp1) << 14);
						BasicFunctions.scaleVector(variables.downsampled, 0, variables.downsampled, 0, variables.sqrtEnChange, variables.plcBlock - 16, 14);
						variables.increment = (2048 - (variables.sqrtEnChange >> 3));
						variables.window = 0;
						variables.tempIndex = variables.downsampled[variables.plcBlock - 16];

						for (variables.i = 16; variables.i > 0; variables.i--, variables.window += variables.increment, variables.tempIndex++)
							variables.downsampled[variables.tempIndex] = (short) ((variables.downsampled[variables.tempIndex] * (variables.sqrtEnChange + (variables.window >> 1))) >> 14);
					}

					if (variables.plcBlock == 40)
						variables.increment = 400;
					else variables.increment = 202;

					variables.window = variables.increment;
					variables.tempIndex = 640 - 1 - mode.size;
					for (variables.i = 0; variables.i < variables.plcBlock; variables.i++, variables.tempIndex--, variables.window += variables.increment)
						variables.enhancementBuffer[variables.tempIndex] = (short) (((variables.enhancementBuffer[variables.tempIndex] * variables.window) >> 14) + (((16384 - variables.window) * variables.downsampled[variables.plcBlock - 1 - variables.i]) >> 14));
				}
				else
				{
					variables.syntIndex = 10;
					variables.tempIndex = 640 - 1 - mode.size - variables.plcBlock;
					System.arraycopy(variables.downsampled, 0, variables.enhancementBuffer, variables.tempIndex, variables.plcBlock);
					System.arraycopy(ILBC.emptyArray, 0, decoderState.synthMem, 0, 11);
					System.arraycopy(ILBC.emptyArray, 0, decoderState.hpiMemX, 0, 4);
					System.arraycopy(ILBC.emptyArray, 0, decoderState.hpiMemY, 0, 2);
					System.arraycopy(decoderState.synthMem, 0, variables.downsampled, 0, 10);
					BasicFunctions.filterAR(variables.enhancementBuffer, variables.tempIndex, variables.downsampled, variables.syntIndex, decoderState.oldSyntDenum, (mode.subframes - 1) * 11, 11, variables.lag);
					System.arraycopy(variables.downsampled, variables.lag, variables.downsampled, 0, 10);
					hpOutput(variables.downsampled, variables.syntIndex, ILBC.HP_OUT_COEFICIENTS, decoderState.hpiMemY, decoderState.hpiMemX, (short) variables.lag, variables.hpOutputVariables);
					BasicFunctions.filterAR(variables.enhancementBuffer, variables.tempIndex, variables.downsampled, variables.syntIndex, decoderState.oldSyntDenum, (mode.subframes - 1) * 11, 11, variables.lag);
					System.arraycopy(variables.downsampled, 0, decoderState.synthMem, 0, 10);
					hpOutput(variables.downsampled, variables.syntIndex, ILBC.HP_OUT_COEFICIENTS, decoderState.hpiMemY, decoderState.hpiMemX, (short) variables.lag, variables.hpOutputVariables);
				}
			}

			for (variables.iBlock = 0; variables.iBlock < variables.newBlocks; variables.iBlock++)
			{
				System.arraycopy(ILBC.emptyArray, 0, variables.surround, 0, 80);
				getSyncSeq(variables.enhancementBuffer, 0, 640, variables.iBlock * 80 + variables.startPos, variables.enhancementPeriod, 0, ILBC.ENHANCEMENT_PLOCS, 0, 8, (short) 3, variables.surround, 0, variables.getSyncSeqVariables);
				smooth(out, outIndex + variables.iBlock * 80, variables.enhancementBuffer, variables.iBlock * 80 + variables.startPos, variables.surround, 0, variables.smoothVariables);
			}

			return (variables.lag);
		}

		public static void getSyncSeq(short[] current, int currentIndex, int currentLength, int centerStartPos, short[] period, int periodIndex, short[] plocs, int plocsIndex, int periodLength, short hl, short[] surround, int surroundIndex, GetSyncSeqVariables variables)
		{
			variables.reset();

			variables.centerEndPos = centerStartPos + 79;
			nearestNeighbor(variables.lagBlock, hl, plocs, plocsIndex, (short) (2 * (centerStartPos + variables.centerEndPos)), periodLength, variables.nearestNeighborVariables);

			variables.blockStartPos[hl] = (short) (4 * centerStartPos);

			for (variables.q = hl - 1; variables.q >= 0; variables.q--)
			{
				variables.blockStartPos[variables.q] = (short) (variables.blockStartPos[variables.q + 1] - period[periodIndex + variables.lagBlock[variables.q + 1]]);
				nearestNeighbor(variables.lagBlock, variables.q, plocs, plocsIndex, (short) (variables.blockStartPos[variables.q] + 160 - period[periodIndex + variables.lagBlock[variables.q + 1]]), periodLength, variables.nearestNeighborVariables);

				if ((variables.blockStartPos[variables.q] - 8) >= 0)
					refiner(variables.blockStartPos, variables.q, current, currentIndex, currentLength, centerStartPos, variables.blockStartPos[variables.q], surround, surroundIndex, ILBC.Enhancement_WT[variables.q], variables.refinerVariables);
			}

			variables.tempIndex1 = plocsIndex;
			variables.tempIndex2 = periodIndex;
			for (variables.i = 0; variables.i < periodLength; variables.i++)
				variables.plocs2[variables.i] = (short) (plocs[variables.tempIndex1++] - period[variables.tempIndex2++]);

			for (variables.q = hl + 1; variables.q <= (2 * hl); variables.q++)
			{

				nearestNeighbor(variables.lagBlock, variables.q, variables.plocs2, 0, (short) (variables.blockStartPos[variables.q - 1] + 160), periodLength, variables.nearestNeighborVariables);
				variables.blockStartPos[variables.q] = (short) (variables.blockStartPos[variables.q - 1] + period[variables.lagBlock[variables.q]]);

				if ((variables.blockStartPos[variables.q] + 328) < (4 * currentLength))
					refiner(variables.blockStartPos, variables.q, current, currentIndex, currentLength, centerStartPos, variables.blockStartPos[variables.q], surround, surroundIndex, ILBC.Enhancement_WT[2 * hl - variables.q], variables.refinerVariables);
			}
		}

		public static void nearestNeighbor(short[] index, int indexIndex, short[] array, int arrayIndex, short value, int arrayLength, NearestNeighborVariables variables)
		{
			variables.reset();

			for (variables.i = 0; variables.i < arrayLength; variables.i++)
			{
				variables.diff = (short) (array[arrayIndex++] - value);
				variables.crit[variables.i] = (variables.diff * variables.diff);
			}

			index[indexIndex] = (short) BasicFunctions.getMinIndex(variables.crit, 0, arrayLength);
		}

		public static void refiner(short[] startPos, int startPosIndex, short[] current, int currentIndex, int currentLength, int centerStartPos, short estimatedSegmentPos, short[] surround, int surroundIndex, short gain, RefinerVariables variables)
		{
			variables.reset();

			variables.estSegPosRounded = (short) ((estimatedSegmentPos - 2) >> 2);
			variables.searchSegStartPos = (short) (variables.estSegPosRounded - 2);

			if (variables.searchSegStartPos < 0)
				variables.searchSegStartPos = 0;

			variables.searchSegEndPos = (short) (variables.estSegPosRounded + 2);

			if (variables.searchSegEndPos + 80 >= currentLength)
				variables.searchSegEndPos = (short) (currentLength - 81);

			variables.corrDim = (short) (variables.searchSegEndPos - variables.searchSegStartPos + 1);

			variables.max = BasicFunctions.getMaxAbsValue(current, currentIndex + variables.searchSegStartPos, variables.corrDim + 79);
			variables.scale = (short) BasicFunctions.getSize(variables.max);
			variables.scale = (short) ((2 * variables.scale) - 26);
			if (variables.scale < 0)
				variables.scale = 0;

			crossCorrelation(variables.corrVecTemp, 0, current, currentIndex + centerStartPos, current, currentIndex + variables.searchSegStartPos, (short) 80, variables.corrDim, variables.scale, (short) 1, variables.crossCorrelationVariables);
			variables.maxTemp = BasicFunctions.getMaxAbsValue(variables.corrVecTemp, 0, variables.corrDim);
			variables.scaleFact = BasicFunctions.getSize(variables.maxTemp) - 15;

			if (variables.scaleFact > 0)
			{
				for (variables.i = 0; variables.i < variables.corrDim; variables.i++)
					variables.corrVec[variables.i] = (short) (variables.corrVecTemp[variables.i] >> variables.scaleFact);
			}
			else
			{
				for (variables.i = 0; variables.i < variables.corrDim; variables.i++)
					variables.corrVec[variables.i] = (short) variables.corrVecTemp[variables.i];
			}

			System.arraycopy(ILBC.emptyArray, 0, variables.corrVec, variables.corrDim, 5 - variables.corrDim);
			enhanceUpSample(variables.corrVecUps, 0, variables.corrVec, 0, variables.enhanceUpSampleVariables);
			variables.tLoc = (short) BasicFunctions.getMaxIndex(variables.corrVecUps, 0, 4 * variables.corrDim);

			startPos[startPosIndex] = (short) (variables.searchSegStartPos * 4 + variables.tLoc + 4);
			variables.tLoc2 = (short) ((variables.tLoc + 3) >> 2);
			variables.st = (short) (variables.searchSegStartPos + variables.tLoc2 - 3);

			if (variables.st < 0)
			{
				System.arraycopy(ILBC.emptyArray, 0, variables.vect, 0, 0 - variables.st);
				System.arraycopy(current, currentIndex, variables.vect, 0 - variables.st, 86 + variables.st);
			}
			else
			{
				variables.en = (short) (variables.st + 86);
				if (variables.en > currentLength)
				{
					System.arraycopy(current, currentIndex + variables.st, variables.vect, 0, 86 - variables.en + currentLength);
					System.arraycopy(ILBC.emptyArray, 0, variables.vect, 86 - variables.en + currentLength, variables.en - currentLength);
				}
				else System.arraycopy(current, currentIndex + variables.st, variables.vect, 0, 86);
			}

			variables.fraction = (short) (variables.tLoc2 * 4 - variables.tLoc);
			variables.filterStateIndex = 6;
			variables.polyIndex = variables.fraction;

			for (variables.i = 0; variables.i < 7; variables.i++)
				variables.filt[variables.filterStateIndex--] = ILBC.Enhancement_POLY_PHASER[variables.polyIndex][variables.i];

			BasicFunctions.filterMA(variables.vect, 6, variables.vect, 0, variables.filt, 0, 7, 80);
			BasicFunctions.addAffineVectorToVector(surround, surroundIndex, variables.vect, 0, gain, 32768, (short) 16, 80);
		}

		public static final void enhanceUpSample(int[] useq, int useqIndex, short[] seq, int seqIndex, EnhanceUpSampleVariables variables)
		{
			variables.pu1 = useqIndex;
			for (variables.j = 0; variables.j < 4; variables.j++)
			{
				variables.pu11 = variables.pu1;
				variables.pp = 1;
				variables.ps = seqIndex + 2;
				useq[variables.pu11] = (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);

				variables.pu11 += 4;
				variables.pp = 1;
				variables.ps = seqIndex + 3;
				useq[variables.pu11] = (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);

				variables.pu11 += 4;
				variables.pp = 1;
				variables.ps = seqIndex + 4;
				useq[variables.pu11] = (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				variables.pu1++;
			}

			variables.pu1 = useqIndex + 12;
			for (variables.j = 0; variables.j < 4; variables.j++)
			{
				variables.pu11 = variables.pu1;
				variables.pp = 2;
				variables.ps = seqIndex + 4;
				useq[variables.pu11] = (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);

				variables.pu11 += 4;
				variables.pp = 3;
				variables.ps = seqIndex + 4;
				useq[variables.pu11] = (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				useq[variables.pu11] += (seq[variables.ps--] * ILBC.Enhancement_POLY_PHASER[variables.j][variables.pp++]);
				variables.pu1++;
			}
		}

		public static void smooth(short[] out, int outIndex, short[] current, int currentIndex, short[] surround, int surroundIndex, SmoothVariables variables)
		{
			variables.w00 = variables.w10 = variables.w11 = 0;
			variables.max1 = BasicFunctions.getMaxAbsValue(current, currentIndex, 80);
			variables.maxTotal = BasicFunctions.getMaxAbsValue(surround, surroundIndex, 80);

			if (variables.max1 > variables.maxTotal)
				variables.maxTotal = variables.max1;

			variables.scale = BasicFunctions.getSize(variables.maxTotal);
			variables.scale = (short) ((2 * variables.scale) - 26);
			if (variables.scale < 0)
				variables.scale = 0;

			variables.w00 = BasicFunctions.scaleRight(current, currentIndex, current, currentIndex, 80, variables.scale);
			variables.w11 = BasicFunctions.scaleRight(surround, surroundIndex, surround, surroundIndex, 80, variables.scale);
			variables.w10 = BasicFunctions.scaleRight(surround, surroundIndex, current, currentIndex, 80, variables.scale);

			if (variables.w00 < 0)
				variables.w00 = Integer.MAX_VALUE;
			if (variables.w11 < 0)
				variables.w11 = Integer.MAX_VALUE;

			variables.bitsW00 = BasicFunctions.getSize(variables.w00);
			variables.bitsW11 = BasicFunctions.getSize(variables.w11);
			if (variables.w10 > 0)
				variables.bitsW10 = BasicFunctions.getSize(variables.w10);
			else variables.bitsW10 = BasicFunctions.getSize(-variables.w10);

			variables.scale1 = (short) (31 - variables.bitsW00);
			variables.scale2 = (short) (15 - variables.bitsW11);

			if (variables.scale2 > (variables.scale1 - 16))
				variables.scale2 = (short) (variables.scale1 - 16);
			else variables.scale1 = (short) (variables.scale2 + 16);

			variables.w00Prim = (variables.w00 << variables.scale1);
			if (variables.scale2 > 0)
				variables.w11Prim = (short) (variables.w11 >> variables.scale2);
			else variables.w11Prim = (short) (variables.w11 << variables.scale2);

			if (variables.w11Prim > 64)
			{
				variables.endiff = ((variables.w00Prim / variables.w11Prim) << 6);
				variables.C = (short) BasicFunctions.sqrtFloor(variables.endiff);
			}
			else variables.C = 1;

			variables.tempIndex1 = outIndex;
			variables.tempIndex2 = surroundIndex;
			for (variables.i = 0; variables.i < 80; variables.i++)
				out[variables.tempIndex1++] = (short) (((variables.C * surround[variables.tempIndex2++]) + 1024) >> 11);

			variables.errors = 0;
			variables.tempIndex1 = outIndex;
			variables.tempIndex2 = currentIndex;
			for (variables.i = 0; variables.i < 80; variables.i++)
			{
				variables.error = (short) ((current[variables.tempIndex2++] - out[variables.tempIndex1++]) >> 3);
				variables.errors += (variables.error * variables.error);
			}

			if ((6 - variables.scale + variables.scale1) > 31)
				variables.crit = 0;
			else variables.crit = ((819 * (variables.w00Prim >> 14)) << ((6 - variables.scale + variables.scale1)));

			if (variables.errors > variables.crit)
			{
				if (variables.w00 < 1)
					variables.w00 = 1;

				variables.scale1 = (short) (variables.bitsW00 - 15);
				variables.scale2 = (short) (variables.bitsW11 - 15);

				if (variables.scale2 > variables.scale1)
					variables.scale = variables.scale2;
				else variables.scale = variables.scale1;

				variables.w11W00 = (variables.w11 << variables.scale) * (variables.w00 << variables.scale);
				variables.w10W10 = (variables.w10 << variables.scale) * (variables.w10 << variables.scale);
				variables.w00W00 = (variables.w00 << variables.scale) * (variables.w00 << variables.scale);

				if (variables.w00W00 > 65536)
				{
					variables.endiff = (variables.w11W00 - variables.w10W10);
					if (variables.endiff < 0)
						variables.endiff = 0;

					variables.denom = (variables.endiff / (variables.w00W00 >> 16));
				}
				else variables.denom = 65536;

				if (variables.denom > 7)
				{
					variables.scale = (short) (BasicFunctions.getSize(variables.denom) - 15);

					if (variables.scale > 0)
					{
						variables.denom16 = (short) (variables.denom >> variables.scale);
						variables.num = (848256041 >> variables.scale);
					}
					else
					{
						variables.denom16 = (short) variables.denom;
						variables.num = 848256041;
					}

					variables.A = (short) BasicFunctions.sqrtFloor(variables.num / variables.denom16);

					variables.scale1 = (short) (31 - variables.bitsW10);
					variables.scale2 = (short) (21 - variables.scale1);
					variables.w10Prim = (variables.w10 << variables.scale1);
					variables.w00Prim = (variables.w00 << variables.scale2);
					variables.scale = (short) (variables.bitsW00 - variables.scale2 - 15);

					if (variables.scale > 0)
					{
						variables.w10Prim = (variables.w10Prim >> variables.scale);
						variables.w00Prim = (variables.w00Prim >> variables.scale);
					}

					if (variables.w00Prim > 0 && variables.w10Prim > 0)
					{
						variables.w11DivW00 = (variables.w10Prim / variables.w00Prim);

						if (BasicFunctions.getSize(variables.w11DivW00) + BasicFunctions.getSize(variables.A) > 31)
							variables.B32 = 0;
						else variables.B32 = 1073741824 - 26843546 - (variables.A * variables.w11DivW00);

						variables.B = (short) (variables.B32 >> 16);
					}
					else
					{
						variables.A = 0;
						variables.B = 16384;
					}
				}
				else
				{
					variables.A = 0;
					variables.B = 16384;
				}

				BasicFunctions.scaleAndAddVectors(surround, surroundIndex, variables.A, 9, current, currentIndex, variables.B, 14, out, outIndex, 80);
			}
		}
	}

	static final short[] emptyArray = new short[643];
	static final int[] emptyIntArray = new int[128];
	static final short bestIndexMin = (short) -21299;
	static final short bestIndexMax = (short) 21299;
	static final int bestIndexMinI = -21299;
	static final int bestIndexMaxI = 21299;
	static final short EPS = 319;
	static final short HALF_EPS = 160;
	static final short MAX_LSF = 25723;
	static final short MIN_LSF = 82;
	static final short HP_IN_COEFICIENTS[] = { (short) 3798, (short) -7596, (short) 3798, (short) 7807, (short) -3733 };
	static final short HP_OUT_COEFICIENTS[] = { (short) 3849, (short) -7699, (short) 3849, (short) 7918, (short) -3833 };
	static final int LPC_LAG_WIN[] = { 2147483647, 2144885453, 2137754373, 2125918626, 2109459810, 2088483140, 2063130336, 2033564590, 1999977009, 1962580174, 1921610283 };
	static final short COS[] = { (short) 32767, (short) 32729, (short) 32610, (short) 32413, (short) 32138, (short) 31786, (short) 31357, (short) 30853, (short) 30274, (short) 29622, (short) 28899, (short) 28106, (short) 27246, (short) 26320, (short) 25330, (short) 24279, (short) 23170, (short) 22006, (short) 20788, (short) 19520, (short) 18205, (short) 16846, (short) 15447, (short) 14010, (short) 12540, (short) 11039, (short) 9512, (short) 7962, (short) 6393, (short) 4808, (short) 3212, (short) 1608, (short) 0, (short) -1608, (short) -3212, (short) -4808, (short) -6393, (short) -7962,
			(short) -9512, (short) -11039, (short) -12540, (short) -14010, (short) -15447, (short) -16846, (short) -18205, (short) -19520, (short) -20788, (short) -22006, (short) -23170, (short) -24279, (short) -25330, (short) -26320, (short) -27246, (short) -28106, (short) -28899, (short) -29622, (short) -30274, (short) -30853, (short) -31357, (short) -31786, (short) -32138, (short) -32413, (short) -32610, (short) -32729 };
	static final short COS_GRID[] = { (short) 32760, (short) 32723, (short) 32588, (short) 32364, (short) 32051, (short) 31651, (short) 31164, (short) 30591, (short) 29935, (short) 29196, (short) 28377, (short) 27481, (short) 26509, (short) 25465, (short) 24351, (short) 23170, (short) 21926, (short) 20621, (short) 19260, (short) 17846, (short) 16384, (short) 14876, (short) 13327, (short) 11743, (short) 10125, (short) 8480, (short) 6812, (short) 5126, (short) 3425, (short) 1714, (short) 0, (short) -1714, (short) -3425, (short) -5126, (short) -6812, (short) -8480, (short) -10125, (short) -11743,
			(short) -13327, (short) -14876, (short) -16384, (short) -17846, (short) -19260, (short) -20621, (short) -21926, (short) -23170, (short) -24351, (short) -25465, (short) -26509, (short) -27481, (short) -28377, (short) -29196, (short) -29935, (short) -30591, (short) -31164, (short) -31651, (short) -32051, (short) -32364, (short) -32588, (short) -32723, (short) -32760 };
	static final short LPC_CHIRP_WEIGHT_DENUM[] = { (short) 32767, (short) 13835, (short) 5841, (short) 2466, (short) 1041, (short) 440, (short) 186, (short) 78, (short) 33, (short) 14, (short) 6 };
	static final short LPC_ASYM_WIN[] = { (short) 2, (short) 7, (short) 15, (short) 27, (short) 42, (short) 60, (short) 81, (short) 106, (short) 135, (short) 166, (short) 201, (short) 239, (short) 280, (short) 325, (short) 373, (short) 424, (short) 478, (short) 536, (short) 597, (short) 661, (short) 728, (short) 798, (short) 872, (short) 949, (short) 1028, (short) 1111, (short) 1197, (short) 1287, (short) 1379, (short) 1474, (short) 1572, (short) 1674, (short) 1778, (short) 1885, (short) 1995, (short) 2108, (short) 2224, (short) 2343, (short) 2465, (short) 2589, (short) 2717, (short) 2847,
			(short) 2980, (short) 3115, (short) 3254, (short) 3395, (short) 3538, (short) 3684, (short) 3833, (short) 3984, (short) 4138, (short) 4295, (short) 4453, (short) 4615, (short) 4778, (short) 4944, (short) 5112, (short) 5283, (short) 5456, (short) 5631, (short) 5808, (short) 5987, (short) 6169, (short) 6352, (short) 6538, (short) 6725, (short) 6915, (short) 7106, (short) 7300, (short) 7495, (short) 7692, (short) 7891, (short) 8091, (short) 8293, (short) 8497, (short) 8702, (short) 8909, (short) 9118, (short) 9328, (short) 9539, (short) 9752, (short) 9966, (short) 10182,
			(short) 10398, (short) 10616, (short) 10835, (short) 11055, (short) 11277, (short) 11499, (short) 11722, (short) 11947, (short) 12172, (short) 12398, (short) 12625, (short) 12852, (short) 13080, (short) 13309, (short) 13539, (short) 13769, (short) 14000, (short) 14231, (short) 14463, (short) 14695, (short) 14927, (short) 15160, (short) 15393, (short) 15626, (short) 15859, (short) 16092, (short) 16326, (short) 16559, (short) 16792, (short) 17026, (short) 17259, (short) 17492, (short) 17725, (short) 17957, (short) 18189, (short) 18421, (short) 18653, (short) 18884, (short) 19114,
			(short) 19344, (short) 19573, (short) 19802, (short) 20030, (short) 20257, (short) 20483, (short) 20709, (short) 20934, (short) 21157, (short) 21380, (short) 21602, (short) 21823, (short) 22042, (short) 22261, (short) 22478, (short) 22694, (short) 22909, (short) 23123, (short) 23335, (short) 23545, (short) 23755, (short) 23962, (short) 24168, (short) 24373, (short) 24576, (short) 24777, (short) 24977, (short) 25175, (short) 25371, (short) 25565, (short) 25758, (short) 25948, (short) 26137, (short) 26323, (short) 26508, (short) 26690, (short) 26871, (short) 27049, (short) 27225,
			(short) 27399, (short) 27571, (short) 27740, (short) 27907, (short) 28072, (short) 28234, (short) 28394, (short) 28552, (short) 28707, (short) 28860, (short) 29010, (short) 29157, (short) 29302, (short) 29444, (short) 29584, (short) 29721, (short) 29855, (short) 29987, (short) 30115, (short) 30241, (short) 30364, (short) 30485, (short) 30602, (short) 30717, (short) 30828, (short) 30937, (short) 31043, (short) 31145, (short) 31245, (short) 31342, (short) 31436, (short) 31526, (short) 31614, (short) 31699, (short) 31780, (short) 31858, (short) 31933, (short) 32005, (short) 32074,
			(short) 32140, (short) 32202, (short) 32261, (short) 32317, (short) 32370, (short) 32420, (short) 32466, (short) 32509, (short) 32549, (short) 32585, (short) 32618, (short) 32648, (short) 32675, (short) 32698, (short) 32718, (short) 32734, (short) 32748, (short) 32758, (short) 32764, (short) 32767, (short) 32767, (short) 32667, (short) 32365, (short) 31863, (short) 31164, (short) 30274, (short) 29197, (short) 27939, (short) 26510, (short) 24917, (short) 23170, (short) 21281, (short) 19261, (short) 17121, (short) 14876, (short) 12540, (short) 10126, (short) 7650, (short) 5126,
			(short) 2571 };
	static final short LPC_CHIRP_SYNT_DENUM[] = { (short) 32767, (short) 29573, (short) 26690, (short) 24087, (short) 21739, (short) 19619, (short) 17707, (short) 15980, (short) 14422, (short) 13016, (short) 11747 };
	static final short LSP_MEAN[] = { (short) 31476, (short) 29565, (short) 25819, (short) 18725, (short) 10276, (short) 1236, (short) -9049, (short) -17600, (short) -25884, (short) -30618 };
	static final short LSF_WEIGHT_20MS[] = { (short) 12288, (short) 8192, (short) 4096, (short) 0 };
	static final short LSF_CB[] = { (short) 1273, (short) 2238, (short) 3696, (short) 3199, (short) 5309, (short) 8209, (short) 3606, (short) 5671, (short) 7829, (short) 2815, (short) 5262, (short) 8778, (short) 2608, (short) 4027, (short) 5493, (short) 1582, (short) 3076, (short) 5945, (short) 2983, (short) 4181, (short) 5396, (short) 2437, (short) 4322, (short) 6902, (short) 1861, (short) 2998, (short) 4613, (short) 2007, (short) 3250, (short) 5214, (short) 1388, (short) 2459, (short) 4262, (short) 2563, (short) 3805, (short) 5269, (short) 2036, (short) 3522, (short) 5129, (short) 1935,
			(short) 4025, (short) 6694, (short) 2744, (short) 5121, (short) 7338, (short) 2810, (short) 4248, (short) 5723, (short) 3054, (short) 5405, (short) 7745, (short) 1449, (short) 2593, (short) 4763, (short) 3411, (short) 5128, (short) 6596, (short) 2484, (short) 4659, (short) 7496, (short) 1668, (short) 2879, (short) 4818, (short) 1812, (short) 3072, (short) 5036, (short) 1638, (short) 2649, (short) 3900, (short) 2464, (short) 3550, (short) 4644, (short) 1853, (short) 2900, (short) 4158, (short) 2458, (short) 4163, (short) 5830, (short) 2556, (short) 4036, (short) 6254, (short) 2703,
			(short) 4432, (short) 6519, (short) 3062, (short) 4953, (short) 7609, (short) 1725, (short) 3703, (short) 6187, (short) 2221, (short) 3877, (short) 5427, (short) 2339, (short) 3579, (short) 5197, (short) 2021, (short) 4633, (short) 7037, (short) 2216, (short) 3328, (short) 4535, (short) 2961, (short) 4739, (short) 6667, (short) 2807, (short) 3955, (short) 5099, (short) 2788, (short) 4501, (short) 6088, (short) 1642, (short) 2755, (short) 4431, (short) 3341, (short) 5282, (short) 7333, (short) 2414, (short) 3726, (short) 5727, (short) 1582, (short) 2822, (short) 5269, (short) 2259,
			(short) 3447, (short) 4905, (short) 3117, (short) 4986, (short) 7054, (short) 1825, (short) 3491, (short) 5542, (short) 3338, (short) 5736, (short) 8627, (short) 1789, (short) 3090, (short) 5488, (short) 2566, (short) 3720, (short) 4923, (short) 2846, (short) 4682, (short) 7161, (short) 1950, (short) 3321, (short) 5976, (short) 1834, (short) 3383, (short) 6734, (short) 3238, (short) 4769, (short) 6094, (short) 2031, (short) 3978, (short) 5903, (short) 1877, (short) 4068, (short) 7436, (short) 2131, (short) 4644, (short) 8296, (short) 2764, (short) 5010, (short) 8013, (short) 2194,
			(short) 3667, (short) 6302, (short) 2053, (short) 3127, (short) 4342, (short) 3523, (short) 6595, (short) 10010, (short) 3134, (short) 4457, (short) 5748, (short) 3142, (short) 5819, (short) 9414, (short) 2223, (short) 4334, (short) 6353, (short) 2022, (short) 3224, (short) 4822, (short) 2186, (short) 3458, (short) 5544, (short) 2552, (short) 4757, (short) 6870,

			(short) 10905, (short) 12917, (short) 14578, (short) 9503, (short) 11485, (short) 14485, (short) 9518, (short) 12494, (short) 14052, (short) 6222, (short) 7487, (short) 9174, (short) 7759, (short) 9186, (short) 10506, (short) 8315, (short) 12755, (short) 14786, (short) 9609, (short) 11486, (short) 13866, (short) 8909, (short) 12077, (short) 13643, (short) 7369, (short) 9054, (short) 11520, (short) 9408, (short) 12163, (short) 14715, (short) 6436, (short) 9911, (short) 12843, (short) 7109, (short) 9556, (short) 11884, (short) 7557, (short) 10075, (short) 11640, (short) 6482,
			(short) 9202, (short) 11547, (short) 6463, (short) 7914, (short) 10980, (short) 8611, (short) 10427, (short) 12752, (short) 7101, (short) 9676, (short) 12606, (short) 7428, (short) 11252, (short) 13172, (short) 10197, (short) 12955, (short) 15842, (short) 7487, (short) 10955, (short) 12613, (short) 5575, (short) 7858, (short) 13621, (short) 7268, (short) 11719, (short) 14752, (short) 7476, (short) 11744, (short) 13795, (short) 7049, (short) 8686, (short) 11922, (short) 8234, (short) 11314, (short) 13983, (short) 6560, (short) 11173, (short) 14984, (short) 6405, (short) 9211,
			(short) 12337, (short) 8222, (short) 12054, (short) 13801, (short) 8039, (short) 10728, (short) 13255, (short) 10066, (short) 12733, (short) 14389, (short) 6016, (short) 7338, (short) 10040, (short) 6896, (short) 8648, (short) 10234, (short) 7538, (short) 9170, (short) 12175, (short) 7327, (short) 12608, (short) 14983, (short) 10516, (short) 12643, (short) 15223, (short) 5538, (short) 7644, (short) 12213, (short) 6728, (short) 12221, (short) 14253, (short) 7563, (short) 9377, (short) 12948, (short) 8661, (short) 11023, (short) 13401, (short) 7280, (short) 8806, (short) 11085,
			(short) 7723, (short) 9793, (short) 12333, (short) 12225, (short) 14648, (short) 16709, (short) 8768, (short) 13389, (short) 15245, (short) 10267, (short) 12197, (short) 13812, (short) 5301, (short) 7078, (short) 11484, (short) 7100, (short) 10280, (short) 11906, (short) 8716, (short) 12555, (short) 14183, (short) 9567, (short) 12464, (short) 15434, (short) 7832, (short) 12305, (short) 14300, (short) 7608, (short) 10556, (short) 12121, (short) 8913, (short) 11311, (short) 12868, (short) 7414, (short) 9722, (short) 11239, (short) 8666, (short) 11641, (short) 13250, (short) 9079,
			(short) 10752, (short) 12300, (short) 8024, (short) 11608, (short) 13306, (short) 10453, (short) 13607, (short) 16449, (short) 8135, (short) 9573, (short) 10909, (short) 6375, (short) 7741, (short) 10125, (short) 10025, (short) 12217, (short) 14874, (short) 6985, (short) 11063, (short) 14109, (short) 9296, (short) 13051, (short) 14642, (short) 8613, (short) 10975, (short) 12542, (short) 6583, (short) 10414, (short) 13534, (short) 6191, (short) 9368, (short) 13430, (short) 5742, (short) 6859, (short) 9260, (short) 7723, (short) 9813, (short) 13679, (short) 8137, (short) 11291,
			(short) 12833, (short) 6562, (short) 8973, (short) 10641, (short) 6062, (short) 8462, (short) 11335, (short) 6928, (short) 8784, (short) 12647, (short) 7501, (short) 8784, (short) 10031, (short) 8372, (short) 10045, (short) 12135, (short) 8191, (short) 9864, (short) 12746, (short) 5917, (short) 7487, (short) 10979, (short) 5516, (short) 6848, (short) 10318, (short) 6819, (short) 9899, (short) 11421, (short) 7882, (short) 12912, (short) 15670, (short) 9558, (short) 11230, (short) 12753, (short) 7752, (short) 9327, (short) 11472, (short) 8479, (short) 9980, (short) 11358,
			(short) 11418, (short) 14072, (short) 16386, (short) 7968, (short) 10330, (short) 14423, (short) 8423, (short) 10555, (short) 12162, (short) 6337, (short) 10306, (short) 14391, (short) 8850, (short) 10879, (short) 14276, (short) 6750, (short) 11885, (short) 15710, (short) 7037, (short) 8328, (short) 9764, (short) 6914, (short) 9266, (short) 13476, (short) 9746, (short) 13949, (short) 15519, (short) 11032, (short) 14444, (short) 16925, (short) 8032, (short) 10271, (short) 11810, (short) 10962, (short) 13451, (short) 15833, (short) 10021, (short) 11667, (short) 13324, (short) 6273,
			(short) 8226, (short) 12936, (short) 8543, (short) 10397, (short) 13496, (short) 7936, (short) 10302, (short) 12745, (short) 6769, (short) 8138, (short) 10446, (short) 6081, (short) 7786, (short) 11719, (short) 8637, (short) 11795, (short) 14975, (short) 8790, (short) 10336, (short) 11812, (short) 7040, (short) 8490, (short) 10771, (short) 7338, (short) 10381, (short) 13153, (short) 6598, (short) 7888, (short) 9358, (short) 6518, (short) 8237, (short) 12030, (short) 9055, (short) 10763, (short) 12983, (short) 6490, (short) 10009, (short) 12007, (short) 9589, (short) 12023,
			(short) 13632, (short) 6867, (short) 9447, (short) 10995, (short) 7930, (short) 9816, (short) 11397, (short) 10241, (short) 13300, (short) 14939, (short) 5830, (short) 8670, (short) 12387, (short) 9870, (short) 11915, (short) 14247, (short) 9318, (short) 11647, (short) 13272, (short) 6721, (short) 10836, (short) 12929, (short) 6543, (short) 8233, (short) 9944, (short) 8034, (short) 10854, (short) 12394, (short) 9112, (short) 11787, (short) 14218, (short) 9302, (short) 11114, (short) 13400, (short) 9022, (short) 11366, (short) 13816, (short) 6962, (short) 10461, (short) 12480,
			(short) 11288, (short) 13333, (short) 15222, (short) 7249, (short) 8974, (short) 10547, (short) 10566, (short) 12336, (short) 14390, (short) 6697, (short) 11339, (short) 13521, (short) 11851, (short) 13944, (short) 15826, (short) 6847, (short) 8381, (short) 11349, (short) 7509, (short) 9331, (short) 10939, (short) 8029, (short) 9618, (short) 11909,

			(short) 13973, (short) 17644, (short) 19647, (short) 22474, (short) 14722, (short) 16522, (short) 20035, (short) 22134, (short) 16305, (short) 18179, (short) 21106, (short) 23048, (short) 15150, (short) 17948, (short) 21394, (short) 23225, (short) 13582, (short) 15191, (short) 17687, (short) 22333, (short) 11778, (short) 15546, (short) 18458, (short) 21753, (short) 16619, (short) 18410, (short) 20827, (short) 23559, (short) 14229, (short) 15746, (short) 17907, (short) 22474, (short) 12465, (short) 15327, (short) 20700, (short) 22831, (short) 15085, (short) 16799, (short) 20182,
			(short) 23410, (short) 13026, (short) 16935, (short) 19890, (short) 22892, (short) 14310, (short) 16854, (short) 19007, (short) 22944, (short) 14210, (short) 15897, (short) 18891, (short) 23154, (short) 14633, (short) 18059, (short) 20132, (short) 22899, (short) 15246, (short) 17781, (short) 19780, (short) 22640, (short) 16396, (short) 18904, (short) 20912, (short) 23035, (short) 14618, (short) 17401, (short) 19510, (short) 21672, (short) 15473, (short) 17497, (short) 19813, (short) 23439, (short) 18851, (short) 20736, (short) 22323, (short) 23864, (short) 15055, (short) 16804,
			(short) 18530, (short) 20916, (short) 16490, (short) 18196, (short) 19990, (short) 21939, (short) 11711, (short) 15223, (short) 21154, (short) 23312, (short) 13294, (short) 15546, (short) 19393, (short) 21472, (short) 12956, (short) 16060, (short) 20610, (short) 22417, (short) 11628, (short) 15843, (short) 19617, (short) 22501, (short) 14106, (short) 16872, (short) 19839, (short) 22689, (short) 15655, (short) 18192, (short) 20161, (short) 22452, (short) 12953, (short) 15244, (short) 20619, (short) 23549, (short) 15322, (short) 17193, (short) 19926, (short) 21762, (short) 16873,
			(short) 18676, (short) 20444, (short) 22359, (short) 14874, (short) 17871, (short) 20083, (short) 21959, (short) 11534, (short) 14486, (short) 19194, (short) 21857, (short) 17766, (short) 19617, (short) 21338, (short) 23178, (short) 13404, (short) 15284, (short) 19080, (short) 23136, (short) 15392, (short) 17527, (short) 19470, (short) 21953, (short) 14462, (short) 16153, (short) 17985, (short) 21192, (short) 17734, (short) 19750, (short) 21903, (short) 23783, (short) 16973, (short) 19096, (short) 21675, (short) 23815, (short) 16597, (short) 18936, (short) 21257, (short) 23461,
			(short) 15966, (short) 17865, (short) 20602, (short) 22920, (short) 15416, (short) 17456, (short) 20301, (short) 22972, (short) 18335, (short) 20093, (short) 21732, (short) 23497, (short) 15548, (short) 17217, (short) 20679, (short) 23594, (short) 15208, (short) 16995, (short) 20816, (short) 22870, (short) 13890, (short) 18015, (short) 20531, (short) 22468, (short) 13211, (short) 15377, (short) 19951, (short) 22388, (short) 12852, (short) 14635, (short) 17978, (short) 22680, (short) 16002, (short) 17732, (short) 20373, (short) 23544, (short) 11373, (short) 14134, (short) 19534,
			(short) 22707, (short) 17329, (short) 19151, (short) 21241, (short) 23462, (short) 15612, (short) 17296, (short) 19362, (short) 22850, (short) 15422, (short) 19104, (short) 21285, (short) 23164, (short) 13792, (short) 17111, (short) 19349, (short) 21370, (short) 15352, (short) 17876, (short) 20776, (short) 22667, (short) 15253, (short) 16961, (short) 18921, (short) 22123, (short) 14108, (short) 17264, (short) 20294, (short) 23246, (short) 15785, (short) 17897, (short) 20010, (short) 21822, (short) 17399, (short) 19147, (short) 20915, (short) 22753, (short) 13010, (short) 15659,
			(short) 18127, (short) 20840, (short) 16826, (short) 19422, (short) 22218, (short) 24084, (short) 18108, (short) 20641, (short) 22695, (short) 24237, (short) 18018, (short) 20273, (short) 22268, (short) 23920, (short) 16057, (short) 17821, (short) 21365, (short) 23665, (short) 16005, (short) 17901, (short) 19892, (short) 23016, (short) 13232, (short) 16683, (short) 21107, (short) 23221, (short) 13280, (short) 16615, (short) 19915, (short) 21829, (short) 14950, (short) 18575, (short) 20599, (short) 22511, (short) 16337, (short) 18261, (short) 20277, (short) 23216, (short) 14306,
			(short) 16477, (short) 21203, (short) 23158, (short) 12803, (short) 17498, (short) 20248, (short) 22014, (short) 14327, (short) 17068, (short) 20160, (short) 22006, (short) 14402, (short) 17461, (short) 21599, (short) 23688, (short) 16968, (short) 18834, (short) 20896, (short) 23055, (short) 15070, (short) 17157, (short) 20451, (short) 22315, (short) 15419, (short) 17107, (short) 21601, (short) 23946, (short) 16039, (short) 17639, (short) 19533, (short) 21424, (short) 16326, (short) 19261, (short) 21745, (short) 23673, (short) 16489, (short) 18534, (short) 21658, (short) 23782,
			(short) 16594, (short) 18471, (short) 20549, (short) 22807, (short) 18973, (short) 21212, (short) 22890, (short) 24278, (short) 14264, (short) 18674, (short) 21123, (short) 23071, (short) 15117, (short) 16841, (short) 19239, (short) 23118, (short) 13762, (short) 15782, (short) 20478, (short) 23230, (short) 14111, (short) 15949, (short) 20058, (short) 22354, (short) 14990, (short) 16738, (short) 21139, (short) 23492, (short) 13735, (short) 16971, (short) 19026, (short) 22158, (short) 14676, (short) 17314, (short) 20232, (short) 22807, (short) 16196, (short) 18146, (short) 20459,
			(short) 22339, (short) 14747, (short) 17258, (short) 19315, (short) 22437, (short) 14973, (short) 17778, (short) 20692, (short) 23367, (short) 15715, (short) 17472, (short) 20385, (short) 22349, (short) 15702, (short) 18228, (short) 20829, (short) 23410, (short) 14428, (short) 16188, (short) 20541, (short) 23630, (short) 16824, (short) 19394, (short) 21365, (short) 23246, (short) 13069, (short) 16392, (short) 18900, (short) 21121, (short) 12047, (short) 16640, (short) 19463, (short) 21689, (short) 14757, (short) 17433, (short) 19659, (short) 23125, (short) 15185, (short) 16930,
			(short) 19900, (short) 22540, (short) 16026, (short) 17725, (short) 19618, (short) 22399, (short) 16086, (short) 18643, (short) 21179, (short) 23472, (short) 15462, (short) 17248, (short) 19102, (short) 21196, (short) 17368, (short) 20016, (short) 22396, (short) 24096, (short) 12340, (short) 14475, (short) 19665, (short) 23362, (short) 13636, (short) 16229, (short) 19462, (short) 22728, (short) 14096, (short) 16211, (short) 19591, (short) 21635, (short) 12152, (short) 14867, (short) 19943, (short) 22301, (short) 14492, (short) 17503, (short) 21002, (short) 22728, (short) 14834,
			(short) 16788, (short) 19447, (short) 21411, (short) 14650, (short) 16433, (short) 19326, (short) 22308, (short) 14624, (short) 16328, (short) 19659, (short) 23204, (short) 13888, (short) 16572, (short) 20665, (short) 22488, (short) 12977, (short) 16102, (short) 18841, (short) 22246, (short) 15523, (short) 18431, (short) 21757, (short) 23738, (short) 14095, (short) 16349, (short) 18837, (short) 20947, (short) 13266, (short) 17809, (short) 21088, (short) 22839, (short) 15427, (short) 18190, (short) 20270, (short) 23143, (short) 11859, (short) 16753, (short) 20935, (short) 22486,
			(short) 12310, (short) 17667, (short) 21736, (short) 23319, (short) 14021, (short) 15926, (short) 18702, (short) 22002, (short) 12286, (short) 15299, (short) 19178, (short) 21126, (short) 15703, (short) 17491, (short) 21039, (short) 23151, (short) 12272, (short) 14018, (short) 18213, (short) 22570, (short) 14817, (short) 16364, (short) 18485, (short) 22598, (short) 17109, (short) 19683, (short) 21851, (short) 23677, (short) 12657, (short) 14903, (short) 19039, (short) 22061, (short) 14713, (short) 16487, (short) 20527, (short) 22814, (short) 14635, (short) 16726, (short) 18763,
			(short) 21715, (short) 15878, (short) 18550, (short) 20718, (short) 22906 };
	static final short LSF_MEAN[] = { (short) 2308, (short) 3652, (short) 5434, (short) 7885, (short) 10255, (short) 12559, (short) 15160, (short) 17513, (short) 20328, (short) 22752 };
	static final short COS_DERIVATIVE[] = { (short) -632, (short) -1893, (short) -3150, (short) -4399, (short) -5638, (short) -6863, (short) -8072, (short) -9261, (short) -10428, (short) -11570, (short) -12684, (short) -13767, (short) -14817, (short) -15832, (short) -16808, (short) -17744, (short) -18637, (short) -19486, (short) -20287, (short) -21039, (short) -21741, (short) -22390, (short) -22986, (short) -23526, (short) -24009, (short) -24435, (short) -24801, (short) -25108, (short) -25354, (short) -25540, (short) -25664, (short) -25726, (short) -25726, (short) -25664, (short) -25540,
			(short) -25354, (short) -25108, (short) -24801, (short) -24435, (short) -24009, (short) -23526, (short) -22986, (short) -22390, (short) -21741, (short) -21039, (short) -20287, (short) -19486, (short) -18637, (short) -17744, (short) -16808, (short) -15832, (short) -14817, (short) -13767, (short) -12684, (short) -11570, (short) -10428, (short) -9261, (short) -8072, (short) -6863, (short) -5638, (short) -4399, (short) -3150, (short) -1893, (short) -632 };
	static final short ACOS_DERIVATIVE[] = { (short) -26887, (short) -8812, (short) -5323, (short) -3813, (short) -2979, (short) -2444, (short) -2081, (short) -1811, (short) -1608, (short) -1450, (short) -1322, (short) -1219, (short) -1132, (short) -1059, (short) -998, (short) -946, (short) -901, (short) -861, (short) -827, (short) -797, (short) -772, (short) -750, (short) -730, (short) -713, (short) -699, (short) -687, (short) -677, (short) -668, (short) -662, (short) -657, (short) -654, (short) -652, (short) -652, (short) -654, (short) -657, (short) -662, (short) -668, (short) -677,
			(short) -687, (short) -699, (short) -713, (short) -730, (short) -750, (short) -772, (short) -797, (short) -827, (short) -861, (short) -901, (short) -946, (short) -998, (short) -1059, (short) -1132, (short) -1219, (short) -1322, (short) -1450, (short) -1608, (short) -1811, (short) -2081, (short) -2444, (short) -2979, (short) -3813, (short) -5323, (short) -8812, (short) -26887 };
	static final short LPC_WIN[] = { (short) 6, (short) 22, (short) 50, (short) 89, (short) 139, (short) 200, (short) 272, (short) 355, (short) 449, (short) 554, (short) 669, (short) 795, (short) 932, (short) 1079, (short) 1237, (short) 1405, (short) 1583, (short) 1771, (short) 1969, (short) 2177, (short) 2395, (short) 2622, (short) 2858, (short) 3104, (short) 3359, (short) 3622, (short) 3894, (short) 4175, (short) 4464, (short) 4761, (short) 5066, (short) 5379, (short) 5699, (short) 6026, (short) 6361, (short) 6702, (short) 7050, (short) 7404, (short) 7764, (short) 8130, (short) 8502,
			(short) 8879, (short) 9262, (short) 9649, (short) 10040, (short) 10436, (short) 10836, (short) 11240, (short) 11647, (short) 12058, (short) 12471, (short) 12887, (short) 13306, (short) 13726, (short) 14148, (short) 14572, (short) 14997, (short) 15423, (short) 15850, (short) 16277, (short) 16704, (short) 17131, (short) 17558, (short) 17983, (short) 18408, (short) 18831, (short) 19252, (short) 19672, (short) 20089, (short) 20504, (short) 20916, (short) 21325, (short) 21730, (short) 22132, (short) 22530, (short) 22924, (short) 23314, (short) 23698, (short) 24078, (short) 24452,
			(short) 24821, (short) 25185, (short) 25542, (short) 25893, (short) 26238, (short) 26575, (short) 26906, (short) 27230, (short) 27547, (short) 27855, (short) 28156, (short) 28450, (short) 28734, (short) 29011, (short) 29279, (short) 29538, (short) 29788, (short) 30029, (short) 30261, (short) 30483, (short) 30696, (short) 30899, (short) 31092, (short) 31275, (short) 31448, (short) 31611, (short) 31764, (short) 31906, (short) 32037, (short) 32158, (short) 32268, (short) 32367, (short) 32456, (short) 32533, (short) 32600, (short) 32655, (short) 32700, (short) 32733, (short) 32755,
			(short) 32767, (short) 32767, (short) 32755, (short) 32733, (short) 32700, (short) 32655, (short) 32600, (short) 32533, (short) 32456, (short) 32367, (short) 32268, (short) 32158, (short) 32037, (short) 31906, (short) 31764, (short) 31611, (short) 31448, (short) 31275, (short) 31092, (short) 30899, (short) 30696, (short) 30483, (short) 30261, (short) 30029, (short) 29788, (short) 29538, (short) 29279, (short) 29011, (short) 28734, (short) 28450, (short) 28156, (short) 27855, (short) 27547, (short) 27230, (short) 26906, (short) 26575, (short) 26238, (short) 25893, (short) 25542,
			(short) 25185, (short) 24821, (short) 24452, (short) 24078, (short) 23698, (short) 23314, (short) 22924, (short) 22530, (short) 22132, (short) 21730, (short) 21325, (short) 20916, (short) 20504, (short) 20089, (short) 19672, (short) 19252, (short) 18831, (short) 18408, (short) 17983, (short) 17558, (short) 17131, (short) 16704, (short) 16277, (short) 15850, (short) 15423, (short) 14997, (short) 14572, (short) 14148, (short) 13726, (short) 13306, (short) 12887, (short) 12471, (short) 12058, (short) 11647, (short) 11240, (short) 10836, (short) 10436, (short) 10040, (short) 9649,
			(short) 9262, (short) 8879, (short) 8502, (short) 8130, (short) 7764, (short) 7404, (short) 7050, (short) 6702, (short) 6361, (short) 6026, (short) 5699, (short) 5379, (short) 5066, (short) 4761, (short) 4464, (short) 4175, (short) 3894, (short) 3622, (short) 3359, (short) 3104, (short) 2858, (short) 2622, (short) 2395, (short) 2177, (short) 1969, (short) 1771, (short) 1583, (short) 1405, (short) 1237, (short) 1079, (short) 932, (short) 795, (short) 669, (short) 554, (short) 449, (short) 355, (short) 272, (short) 200, (short) 139, (short) 89, (short) 50, (short) 22, (short) 6 };

	static final short LSF_WEIGHT_30MS[] = { (short) 8192, (short) 16384, (short) 10923, (short) 5461, (short) 0, (short) 0 };

	static final short SCALE[] = {
			/* Values in Q16 */
			(short) 29485, (short) 25003, (short) 21345, (short) 18316, (short) 15578, (short) 13128, (short) 10973, (short) 9310, (short) 7955, (short) 6762, (short) 5789, (short) 4877, (short) 4255, (short) 3699, (short) 3258, (short) 2904, (short) 2595, (short) 2328, (short) 2123, (short) 1932, (short) 1785, (short) 1631, (short) 1493, (short) 1370, (short) 1260, (short) 1167, (short) 1083,
			/* Values in Q21 */
			(short) 32081, (short) 29611, (short) 27262, (short) 25229, (short) 23432, (short) 21803, (short) 20226, (short) 18883, (short) 17609, (short) 16408, (short) 15311, (short) 14327, (short) 13390, (short) 12513, (short) 11693, (short) 10919, (short) 10163, (short) 9435, (short) 8739, (short) 8100, (short) 7424, (short) 6813, (short) 6192, (short) 5648, (short) 5122, (short) 4639, (short) 4207, (short) 3798, (short) 3404, (short) 3048, (short) 2706, (short) 2348, (short) 2036, (short) 1713, (short) 1393, (short) 1087, (short) 747 };

	static final short STATE_SQ3[] = { /* Values in Q13 */
			(short) -30473, (short) -17838, (short) -9257, (short) -2537, (short) 3639, (short) 10893, (short) 19958, (short) 32636 };

	static final short FRQ_QUANT_MOD[] = {
			/* First 37 values in Q8 */
			(short) 569, (short) 671, (short) 786, (short) 916, (short) 1077, (short) 1278, (short) 1529, (short) 1802, (short) 2109, (short) 2481, (short) 2898, (short) 3440, (short) 3943, (short) 4535, (short) 5149, (short) 5778, (short) 6464, (short) 7208, (short) 7904, (short) 8682, (short) 9397, (short) 10285, (short) 11240, (short) 12246, (short) 13313, (short) 14382, (short) 15492, (short) 16735, (short) 18131, (short) 19693, (short) 21280, (short) 22912, (short) 24624, (short) 26544, (short) 28432, (short) 30488, (short) 32720,
			/* 22 values in Q5 */
			(short) 4383, (short) 4684, (short) 5012, (short) 5363, (short) 5739, (short) 6146, (short) 6603, (short) 7113, (short) 7679, (short) 8285, (short) 9040, (short) 9850, (short) 10838, (short) 11882, (short) 13103, (short) 14467, (short) 15950, (short) 17669, (short) 19712, (short) 22016, (short) 24800, (short) 28576,
			/* 5 values in Q3 */
			(short) 8240, (short) 9792, (short) 12040, (short) 15440, (short) 22472 };

	static final short FILTER_RANGE[] = { (short) 63, (short) 85, (short) 125, (short) 147, (short) 147 };

	static final short CB_FILTERS_REV[] = { (short) -140, (short) 446, (short) -755, (short) 3302, (short) 2922, (short) -590, (short) 343, (short) -138 };

	static final short SEARCH_RANGE[][] = { { (short) 58, (short) 58, (short) 58 }, { (short) 108, (short) 44, (short) 44 }, { (short) 108, (short) 108, (short) 108 }, { (short) 108, (short) 108, (short) 108 }, { (short) 108, (short) 108, (short) 108 } };

	static final short GAIN_SQ3[] = { (short) -16384, (short) -10813, (short) -5407, (short) 0, (short) 4096, (short) 8192, (short) 12288, (short) 16384, (short) 32767 };

	static final short GAIN_SQ4[] = { (short) -17203, (short) -14746, (short) -12288, (short) -9830, (short) -7373, (short) -4915, (short) -2458, (short) 0, (short) 2458, (short) 4915, (short) 7373, (short) 9830, (short) 12288, (short) 14746, (short) 17203, (short) 19661, (short) 32767 };

	static final short GAIN_SQ5[] = { (short) 614, (short) 1229, (short) 1843, (short) 2458, (short) 3072, (short) 3686, (short) 4301, (short) 4915, (short) 5530, (short) 6144, (short) 6758, (short) 7373, (short) 7987, (short) 8602, (short) 9216, (short) 9830, (short) 10445, (short) 11059, (short) 11674, (short) 12288, (short) 12902, (short) 13517, (short) 14131, (short) 14746, (short) 15360, (short) 15974, (short) 16589, (short) 17203, (short) 17818, (short) 18432, (short) 19046, (short) 19661, (short) 32767 };

	static final short GAIN[][] = { GAIN_SQ5, GAIN_SQ4, GAIN_SQ3 };

	static final short GAIN_SQ5_SQ[] = { (short) 23, (short) 92, (short) 207, (short) 368, (short) 576, (short) 829, (short) 1129, (short) 1474, (short) 1866, (short) 2304, (short) 2787, (short) 3317, (short) 3893, (short) 4516, (short) 5184, (short) 5897, (short) 6658, (short) 7464, (short) 8318, (short) 9216, (short) 10160, (short) 11151, (short) 12187, (short) 13271, (short) 14400, (short) 15574, (short) 16796, (short) 18062, (short) 19377, (short) 20736, (short) 22140, (short) 23593 };

	static final short ALPHA[] = { (short) 6554, (short) 13107, (short) 19661, (short) 26214 };

	static final int CHOOSE_FRG_QUANT[] = { 118, 163, 222, 305, 425, 604, 851, 1174, 1617, 2222, 3080, 4191, 5525, 7215, 9193, 11540, 14397, 17604, 21204, 25209, 29863, 35720, 42531, 50375, 59162, 68845, 80108, 93754, 110326, 129488, 150654, 174328, 201962, 233195, 267843, 308239, 354503, 405988, 464251, 531550, 608652, 697516, 802526, 928793, 1080145, 1258120, 1481106, 1760881, 2111111, 2546619, 3078825, 3748642, 4563142, 5573115, 6887601, 8582108, 10797296, 14014513, 18625760, 25529599, 37302935, 58819185, 109782723, Integer.MAX_VALUE };

	static final short LSF_DIM_CB[] = { (short) 3, (short) 3, (short) 4 };

	static final short LSF_SIZE_CB[] = { (short) 64, (short) 128, (short) 128 };

	static final short LSF_INDEX_CB[] = { (short) 0, (short) 192, (short) 576 };

	static final short ENG_START_SEQUENCE[] = { (short) 1638, (short) 1843, (short) 2048, (short) 1843, (short) 1638 };

	static final short PLC_PER_SQR[] = { (short) 839, (short) 1343, (short) 2048, (short) 2998, (short) 4247, (short) 5849 };

	static final short PLC_PITCH_FACT[] = { (short) 0, (short) 5462, (short) 10922, (short) 16384, (short) 21846, (short) 27306 };

	static final short PLC_PF_SLOPE[] = { (short) 26667, (short) 18729, (short) 13653, (short) 10258, (short) 7901, (short) 6214 };

	static final short LP_FILT_COEFS[] = { (short) -273, (short) 512, (short) 1297, (short) 1696, (short) 1297, (short) 512, (short) -273 };

	static final short ENHANCEMENT_PLOCS[] = { (short) 160, (short) 480, (short) 800, (short) 1120, (short) 1440, (short) 1760, (short) 2080, (short) 2400 };

	static final short Enhancement_WT[] = { (short) 4800, (short) 16384, (short) 27968 };

	static final short Enhancement_POLY_PHASER[][] = { { (short) 0, (short) 0, (short) 0, (short) 4096, (short) 0, (short) 0, (short) 0 }, { (short) 64, (short) -315, (short) 1181, (short) 3531, (short) -436, (short) 77, (short) -64 }, { (short) 97, (short) -509, (short) 2464, (short) 2464, (short) -509, (short) 97, (short) -97 }, { (short) 77, (short) -436, (short) 3531, (short) 1181, (short) -315, (short) 64, (short) -77 } };

	public static class Decoder
	{
		private short[] decResidual;
		private short[] plcResidual;
		private short[] syntDenum;
		private short[] output;
		private short[] plcLpc;
		private short[] signal;
		private short[] lsfDeq;
		private short[] weightDenum;

		private EncoderBits encoderBits;
		private DecoderState decoderState;
		private UnpackBitsVariables unpackBitsVariables;
		private UpdateDecIndexVariables updateDecIndexVariables;
		private SimpleLsfDeqVariables simpleLsfDeqVariables;
		private LsfCheckVariables lsfCheckVariables;
		private DecodeInterpolateLsfVariables decodeInterpolateLsfVariables;
		private DecodeResidualVariables decodeResidualVariables;
		private DoThePlcVariables doThePlcVariables;
		private EnhancerVariables enhancerVariables;
		private XCorrCoefVariables xCorrCoefVariables;
		private HpOutputVariables hpOutputVariables;

		private int i, temp;
		private boolean isError;

		private Mode mode;

		public Decoder(Mode mode)
		{
			this.mode = mode;

			this.decResidual = new short[240];
			this.plcResidual = new short[250];
			this.syntDenum = new short[66];
			this.output = new short[240];
			this.plcLpc = new short[11];
			this.signal = new short[25];
			this.lsfDeq = new short[20];
			this.weightDenum = new short[66];

			this.decoderState = new DecoderState();
			this.encoderBits = new EncoderBits();
			this.unpackBitsVariables = new UnpackBitsVariables();
			this.updateDecIndexVariables = new UpdateDecIndexVariables();
			this.simpleLsfDeqVariables = new SimpleLsfDeqVariables();
			this.lsfCheckVariables = new LsfCheckVariables();
			this.decodeInterpolateLsfVariables = new DecodeInterpolateLsfVariables();
			this.decodeResidualVariables = new DecodeResidualVariables();
			this.doThePlcVariables = new DoThePlcVariables();
			this.enhancerVariables = new EnhancerVariables();
			this.xCorrCoefVariables = new XCorrCoefVariables();
			this.hpOutputVariables = new HpOutputVariables();

			this.reset();
		}

		public void reset()
		{
			decoderState.reset();
		}

		public void process(ByteBuffer inputBuffer,ByteBuffer outputBuffer)
		{
			temp = inputBuffer.remaining() / 2;

			for (i = 0; i < temp; i++)
			{
				signal[i] = ((short) ((inputBuffer.get(i * 2) << 8) | (inputBuffer.get(i * 2 + 1) & 0xFF)));
			}

			CodingFunctions.unpackBits(encoderBits, signal, mode.value, unpackBitsVariables);
			isError = (encoderBits.startIdx < 1) || (mode == Mode.MODE_20 && encoderBits.startIdx > 3) || (mode == Mode.MODE_30 && encoderBits.startIdx > 5);

			if (!isError)
			{
				CodingFunctions.updateDecIndex(encoderBits, updateDecIndexVariables);
				CodingFunctions.simpleLsfDeq(lsfDeq, 0, encoderBits.LSF, 0, mode.lpc_n, simpleLsfDeqVariables);
				CodingFunctions.lsfCheck(lsfDeq, 0, 10, lsfCheckVariables);
				CodingFunctions.decoderInterpolateLsf(decoderState, syntDenum, 0, weightDenum, 0, lsfDeq, 0, (short) 10, decodeInterpolateLsfVariables, mode);
				CodingFunctions.decodeResidual(decoderState, encoderBits, decResidual, 0, syntDenum, 0, decodeResidualVariables, mode);
				CodingFunctions.doThePlc(decoderState, plcResidual, 0, plcLpc, 0, (short) 0, decResidual, 0, syntDenum, 11 * (mode.subframes - 1), (short) (decoderState.lastLag), doThePlcVariables, mode);
				System.arraycopy(plcResidual, 0, decResidual, 0, mode.size);
			}
			else
			{
				CodingFunctions.doThePlc(decoderState, plcResidual, 0, plcLpc, 0, (short) 1, decResidual, 0, syntDenum, 0, (short) (decoderState.lastLag), doThePlcVariables, mode);
				System.arraycopy(plcResidual, 0, decResidual, 0, mode.size);

				for (i = 0; i < mode.subframes; i++)
				{
					System.arraycopy(plcLpc, 0, syntDenum, i * 11, 11);
				}
			}

			if (decoderState.useEnhancer == 1)
			{
				if (decoderState.prevEnchPl == 2)
				{
					for (i = 0; i < mode.subframes; i++)
					{
						System.arraycopy(syntDenum, 0, decoderState.oldSyntDenum, i * 11, 11);
					}
				}

				decoderState.lastLag = CodingFunctions.enchancher(decResidual, 0, plcResidual, 10, decoderState, enhancerVariables, mode);
				System.arraycopy(decoderState.synthMem, 0, plcResidual, 0, 10);

				if (mode == Mode.MODE_20)
				{
					BasicFunctions.filterAR(plcResidual, 10, plcResidual, 10, decoderState.oldSyntDenum, (mode.subframes - 1) * 11, 11, 40);
					for (i = 1; i < mode.subframes; i++)
					{
						BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, syntDenum, (i - 1) * 11, 11, 40);
					}
				}
				else // if (decoderState.DECODER_MODE == 30)
				{
					for (i = 0; i < 2; i++)
					{
						BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, decoderState.oldSyntDenum, (i + 4) * 11, 11, 40);
					}
					for (i = 2; i < mode.subframes; i++)
					{
						BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, syntDenum, (i - 2) * 11, 11, 40);
					}
				}
			}
			else
			{
				if (mode == Mode.MODE_20) // if (modeValueInt != 30)
				{
					decoderState.lastLag = CodingFunctions.xCorrCoef(decResidual, mode.size - 60, decResidual, mode.size - 80, (short) 60, (short) 80, (short) 20, (short) -1, xCorrCoefVariables);
				}
				else
				{
					decoderState.lastLag = CodingFunctions.xCorrCoef(decResidual, mode.size - 80, decResidual, mode.size - 100, (short) 80, (short) 100, (short) 20, (short) -1, xCorrCoefVariables);
				}

				System.arraycopy(decResidual, 0, plcResidual, 10, mode.size);
				System.arraycopy(decoderState.synthMem, 0, plcResidual, 0, 10);

				for (i = 0; i < mode.subframes; i++)
				{
					BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, syntDenum, 11 * i, 11, 40);
				}
			}

			System.arraycopy(plcResidual, mode.size, decoderState.synthMem, 0, 10);
			System.arraycopy(plcResidual, 10, output, 0, mode.size);
			CodingFunctions.hpOutput(output, 0, ILBC.HP_OUT_COEFICIENTS, decoderState.hpiMemY, decoderState.hpiMemX, mode.size, hpOutputVariables);
			System.arraycopy(syntDenum, 0, decoderState.oldSyntDenum, 0, mode.subframes * 11);

			if (isError) // if (modeValueInt == 0)
			{
				decoderState.prevEnchPl = 1;
			}
			else
			{
				decoderState.prevEnchPl = 0;
			}

			outputBuffer.clear();
			//outputBuffer.limit(mode.size * 2);

			for (int i = 0; i < mode.size; i++)
			{
				outputBuffer.putShort(output[i]);
			}
		}




		public void processTest(byte[] inData, short[] outData)
		{
			if (inData.length != mode.bytes || outData.length != mode.size)
			{
				throw new IllegalArgumentException("invalid size inData.length != " + mode.bytes + " or outData.length != " + mode.size);
			}

			temp = inData.length / 2;

			for (i = 0; i < temp; i++)
			{
				signal[i] = ((short) ((inData[i * 2] << 8) | (inData[i * 2 + 1] & 0xFF)));
			}

			CodingFunctions.unpackBits(encoderBits, signal, mode.value, unpackBitsVariables);
			isError = (encoderBits.startIdx < 1) || (mode == Mode.MODE_20 && encoderBits.startIdx > 3) || (mode == Mode.MODE_30 && encoderBits.startIdx > 5);

			if (!isError)
			{
				CodingFunctions.updateDecIndex(encoderBits, updateDecIndexVariables);
				CodingFunctions.simpleLsfDeq(lsfDeq, 0, encoderBits.LSF, 0, mode.lpc_n, simpleLsfDeqVariables);
				CodingFunctions.lsfCheck(lsfDeq, 0, 10, lsfCheckVariables);
				CodingFunctions.decoderInterpolateLsf(decoderState, syntDenum, 0, weightDenum, 0, lsfDeq, 0, (short) 10, decodeInterpolateLsfVariables, mode);
				CodingFunctions.decodeResidual(decoderState, encoderBits, decResidual, 0, syntDenum, 0, decodeResidualVariables, mode);
				CodingFunctions.doThePlc(decoderState, plcResidual, 0, plcLpc, 0, (short) 0, decResidual, 0, syntDenum, 11 * (mode.subframes - 1), (short) (decoderState.lastLag), doThePlcVariables, mode);
				System.arraycopy(plcResidual, 0, decResidual, 0, mode.size);
			}
			else
			{
				CodingFunctions.doThePlc(decoderState, plcResidual, 0, plcLpc, 0, (short) 1, decResidual, 0, syntDenum, 0, (short) (decoderState.lastLag), doThePlcVariables, mode);
				System.arraycopy(plcResidual, 0, decResidual, 0, mode.size);

				for (i = 0; i < mode.subframes; i++)
				{
					System.arraycopy(plcLpc, 0, syntDenum, i * 11, 11);
				}
			}

			if (decoderState.useEnhancer == 1)
			{
				if (decoderState.prevEnchPl == 2)
				{
					for (i = 0; i < mode.subframes; i++)
					{
						System.arraycopy(syntDenum, 0, decoderState.oldSyntDenum, i * 11, 11);
					}
				}

				decoderState.lastLag = CodingFunctions.enchancher(decResidual, 0, plcResidual, 10, decoderState, enhancerVariables, mode);
				System.arraycopy(decoderState.synthMem, 0, plcResidual, 0, 10);

				if (mode == Mode.MODE_20)
				{
					BasicFunctions.filterAR(plcResidual, 10, plcResidual, 10, decoderState.oldSyntDenum, (mode.subframes - 1) * 11, 11, 40);
					for (i = 1; i < mode.subframes; i++)
					{
						BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, syntDenum, (i - 1) * 11, 11, 40);
					}
				}
				else // if (decoderState.DECODER_MODE == 30)
				{
					for (i = 0; i < 2; i++)
					{
						BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, decoderState.oldSyntDenum, (i + 4) * 11, 11, 40);
					}
					for (i = 2; i < mode.subframes; i++)
					{
						BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, syntDenum, (i - 2) * 11, 11, 40);
					}
				}
			}
			else
			{
				if (mode == Mode.MODE_20) // if (modeValueInt != 30)
				{
					decoderState.lastLag = CodingFunctions.xCorrCoef(decResidual, mode.size - 60, decResidual, mode.size - 80, (short) 60, (short) 80, (short) 20, (short) -1, xCorrCoefVariables);
				}
				else
				{
					decoderState.lastLag = CodingFunctions.xCorrCoef(decResidual, mode.size - 80, decResidual, mode.size - 100, (short) 80, (short) 100, (short) 20, (short) -1, xCorrCoefVariables);
				}

				System.arraycopy(decResidual, 0, plcResidual, 10, mode.size);
				System.arraycopy(decoderState.synthMem, 0, plcResidual, 0, 10);

				for (i = 0; i < mode.subframes; i++)
				{
					BasicFunctions.filterAR(plcResidual, 10 + 40 * i, plcResidual, 10 + 40 * i, syntDenum, 11 * i, 11, 40);
				}
			}

			System.arraycopy(plcResidual, mode.size, decoderState.synthMem, 0, 10);
			System.arraycopy(plcResidual, 10, output, 0, mode.size);
			CodingFunctions.hpOutput(output, 0, ILBC.HP_OUT_COEFICIENTS, decoderState.hpiMemY, decoderState.hpiMemX, mode.size, hpOutputVariables);
			System.arraycopy(syntDenum, 0, decoderState.oldSyntDenum, 0, mode.subframes * 11);

			if (isError) // if (modeValueInt == 0)
			{
				decoderState.prevEnchPl = 1;
			}
			else
			{
				decoderState.prevEnchPl = 0;
			}
			System.arraycopy(output, 0, outData, 0, outData.length);
		}
	}

	public static class Encoder
	{
		private short[] weightdenum;
		private short[] dataVec;
		private short[] memVec;
		private short[] residual;

		private EncoderState encoderState;
		private EncoderBits encoderBits;
		private CbUpdateIndexData updateIndexData;
		private CbSearchData searchData;
		private HpInputVariables hpInputVariables;
		private LpcEncodeVariables lpcEncodeVariables;
		private FrameClassifyVariables frameClassifyVariables;
		private StateSearchVariables stateSearchVariables;
		private StateConstructVariables stateConstructVariables;
		private CbSearchVariables cbSearchVariables;
		private CbConstructVariables cbConstructVariables;
		private PackBitsVariables packBitsVariables;

		private int n, temp, tempIndex1;
		private int memlGotten, nFor, nBack, index, subCount, subFrame, en1, en2;
		private short diff, startPos, scale, max, tempS;

		private Mode mode;

		public Encoder(Mode mode)
		{
			this.mode = mode;

			this.weightdenum = new short[66];
			this.dataVec = new short[250];
			this.memVec = new short[155];
			this.residual = new short[160];

			this.encoderState = new EncoderState();
			this.encoderBits = new EncoderBits();
			this.updateIndexData = new CbUpdateIndexData();
			this.searchData = new CbSearchData();
			this.hpInputVariables = new HpInputVariables();
			this.lpcEncodeVariables = new LpcEncodeVariables();
			this.frameClassifyVariables = new FrameClassifyVariables();
			this.stateSearchVariables = new StateSearchVariables();
			this.stateConstructVariables = new StateConstructVariables();
			this.cbSearchVariables = new CbSearchVariables();
			this.cbConstructVariables = new CbConstructVariables();
			this.packBitsVariables = new PackBitsVariables();

			reset();
		}

		public void reset()
		{
			encoderState.reset();
		}


		public void process(ByteBuffer inBuffer,byte[] outData)
		{

			ShortBuffer shortBuffer = inBuffer.asShortBuffer();
			shortBuffer.get(dataVec,10,160);


			CodingFunctions.hpInput(encoderState, dataVec, 10, 160, hpInputVariables);
			CodingFunctions.lpcEncode(encoderState, encoderBits, memVec, 4, weightdenum, 0, dataVec, 10, lpcEncodeVariables, mode);
			System.arraycopy(encoderState.anaMem, 0, dataVec, 0, 10);

			for (n = 0; n < mode.subframes; n++)
			{
				BasicFunctions.filterMA(dataVec, 10 + n * 40, residual, n * 40, memVec, 4 + n * 11, 11, 40);
			}

			System.arraycopy(dataVec, 160, encoderState.anaMem, 0, 10);

			encoderBits.startIdx = CodingFunctions.frameClassify(residual, frameClassifyVariables, mode);

			index = (encoderBits.startIdx - 1) * 40;
			max = 0;
			tempIndex1 = index;
			for (n = 0; n < 80; n++)
			{
				tempS = residual[tempIndex1++];
				if (tempS < 0)
				{
					tempS = (short) (0 - tempS);
				}

				if (tempS > max)
				{
					max = tempS;
				}
			}
			scale = BasicFunctions.getSize(max * max);

			scale = (short) (scale - 25);
			if (scale < 0)
			{
				scale = 0;
			}

			diff = (short) (80 - mode.state_short_len);

			en1 = BasicFunctions.scaleRight(residual, index, residual, index, mode.state_short_len, scale);
			index += diff;
			en2 = BasicFunctions.scaleRight(residual, index, residual, index, mode.state_short_len, scale);

			if (en1 > en2)
			{
				encoderBits.stateFirst = true;
				startPos = (short) ((encoderBits.startIdx - 1) * 40);
			}
			else
			{
				encoderBits.stateFirst = false;
				startPos = (short) ((encoderBits.startIdx - 1) * 40 + diff);
			}

			CodingFunctions.stateSearch(encoderState, encoderBits, residual, startPos, memVec, 4 + (encoderBits.startIdx - 1) * 11, weightdenum, (encoderBits.startIdx - 1) * 11, stateSearchVariables, mode);

			CodingFunctions.stateConstruct(encoderBits, memVec, 4 + (encoderBits.startIdx - 1) * 11, residual, startPos, mode.state_short_len, stateConstructVariables);

			if (encoderBits.stateFirst)
			{
				for (n = 4; n < 151 - mode.state_short_len; n++)
				{
					memVec[n] = 0;
				}

				System.arraycopy(residual, startPos, memVec, 151 - mode.state_short_len, mode.state_short_len);

				CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, residual, startPos + mode.state_short_len, memVec, 66, 85, diff, weightdenum, encoderBits.startIdx * 11, 0, 0, 0, cbSearchVariables, mode);
				CodingFunctions.cbConstruct(encoderBits, residual, startPos + mode.state_short_len, memVec, 66, (short) 85, diff, 0, 0, cbConstructVariables);
			}
			else
			{
				BasicFunctions.reverseCopy(dataVec, diff + 9, residual, encoderBits.startIdx * 40 - 40, diff);
				BasicFunctions.reverseCopy(memVec, 150, residual, startPos, mode.state_short_len);

				for (n = 4; n < 151 - mode.state_short_len; n++)
				{
					memVec[n] = 0;
				}

				CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, dataVec, 10, memVec, 66, 85, diff, weightdenum, (encoderBits.startIdx - 1) * 11, 0, 0, 0, cbSearchVariables, mode);
				CodingFunctions.cbConstruct(encoderBits, dataVec, 10, memVec, 66, (short) 85, diff, 0, 0, cbConstructVariables);

				BasicFunctions.reverseCopy(residual, startPos - 1, dataVec, 10, diff);
			}

			nFor = mode.subframes - encoderBits.startIdx - 1;
			subCount = 1;

			if (nFor > 0)
			{
				for (n = 4; n < 71; n++)
				{
					memVec[n] = 0;
				}

				System.arraycopy(residual, (encoderBits.startIdx - 1) * 40, memVec, 71, 80);

				for (subFrame = 0; subFrame < nFor; subFrame++)
				{
					CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, residual, (encoderBits.startIdx + 1 + subFrame) * 40, memVec, 4, 147, 40, weightdenum, (encoderBits.startIdx + 1 + subFrame) * 11, subCount, subCount * 3, subCount * 3, cbSearchVariables, mode);
					CodingFunctions.cbConstruct(encoderBits, residual, (encoderBits.startIdx + 1 + subFrame) * 40, memVec, 4, (short) 147, (short) 40, subCount * 3, subCount * 3, cbConstructVariables);

					temp = 4;
					for (n = 44; n < 151; n++)
					{
						memVec[temp++] = memVec[n];
					}

					System.arraycopy(residual, (encoderBits.startIdx + 1 + subFrame) * 40, memVec, 111, 40);
					subCount++;
				}
			}

			nBack = encoderBits.startIdx - 1;
			if (nBack > 0)
			{
				BasicFunctions.reverseCopy(dataVec, 10 + nBack * 40 - 1, residual, 0, nBack * 40);
				memlGotten = 40 * (mode.subframes + 1 - encoderBits.startIdx);
				if (memlGotten > 147)
				{
					memlGotten = 147;
				}

				BasicFunctions.reverseCopy(memVec, 150, residual, nBack * 40, memlGotten);
				for (n = 4; n < 151 - memlGotten; n++)
				{
					memVec[n] = 0;
				}

				for (subFrame = 0; subFrame < nBack; subFrame++)
				{
					CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, dataVec, 10 + subFrame * 40, memVec, 4, 147, 40, weightdenum, (encoderBits.startIdx - 2 - subFrame) * 11, subCount, subCount * 3, subCount * 3, cbSearchVariables, mode);
					CodingFunctions.cbConstruct(encoderBits, dataVec, 10 + subFrame * 40, memVec, 4, (short) 147, (short) 40, subCount * 3, subCount * 3, cbConstructVariables);
					temp = 4;
					for (n = 44; n < 151; n++)
					{
						memVec[temp++] = memVec[n];
					}

					System.arraycopy(dataVec, 10 + subFrame * 40, memVec, 111, 40);
					subCount++;
				}

				BasicFunctions.reverseCopy(residual, 40 * nBack - 1, dataVec, 10, 40 * nBack);
			}

			short[] index = encoderBits.cbIndex;
			for (n = 4; n < 6; n++)
			{

				if (index[n] >= 108 && index[n] < 172)
				{
					index[n] -= 64;
				}
				else if (index[n] >= 236)
				{
					index[n] -= 128;
				}
				else
				{
					/* ERROR */
				}
			}
			CodingFunctions.packBits(encoderState, encoderBits, outData, packBitsVariables, mode);
		}


		public void processTest(short[] inData, byte[] outData)
		{
			if (inData.length != mode.size || outData.length != mode.bytes)
			{
				throw new IllegalArgumentException("invalid size inData.length != " + mode.size + " or outData.length != " + mode.bytes);
			}


			System.arraycopy(inData, 0, dataVec, 10, 160);
			CodingFunctions.hpInput(encoderState, dataVec, 10, 160, hpInputVariables);
			CodingFunctions.lpcEncode(encoderState, encoderBits, memVec, 4, weightdenum, 0, dataVec, 10, lpcEncodeVariables, mode);
			System.arraycopy(encoderState.anaMem, 0, dataVec, 0, 10);

			for (n = 0; n < mode.subframes; n++)
			{
				BasicFunctions.filterMA(dataVec, 10 + n * 40, residual, n * 40, memVec, 4 + n * 11, 11, 40);
			}

			System.arraycopy(dataVec, 160, encoderState.anaMem, 0, 10);

			encoderBits.startIdx = CodingFunctions.frameClassify(residual, frameClassifyVariables, mode);

			index = (encoderBits.startIdx - 1) * 40;
			max = 0;
			tempIndex1 = index;
			for (n = 0; n < 80; n++)
			{
				tempS = residual[tempIndex1++];
				if (tempS < 0)
				{
					tempS = (short) (0 - tempS);
				}

				if (tempS > max)
				{
					max = tempS;
				}
			}
			scale = BasicFunctions.getSize(max * max);

			scale = (short) (scale - 25);
			if (scale < 0)
			{
				scale = 0;
			}

			diff = (short) (80 - mode.state_short_len);

			en1 = BasicFunctions.scaleRight(residual, index, residual, index, mode.state_short_len, scale);
			index += diff;
			en2 = BasicFunctions.scaleRight(residual, index, residual, index, mode.state_short_len, scale);

			if (en1 > en2)
			{
				encoderBits.stateFirst = true;
				startPos = (short) ((encoderBits.startIdx - 1) * 40);
			}
			else
			{
				encoderBits.stateFirst = false;
				startPos = (short) ((encoderBits.startIdx - 1) * 40 + diff);
			}

			CodingFunctions.stateSearch(encoderState, encoderBits, residual, startPos, memVec, 4 + (encoderBits.startIdx - 1) * 11, weightdenum, (encoderBits.startIdx - 1) * 11, stateSearchVariables, mode);

			CodingFunctions.stateConstruct(encoderBits, memVec, 4 + (encoderBits.startIdx - 1) * 11, residual, startPos, mode.state_short_len, stateConstructVariables);

			if (encoderBits.stateFirst)
			{
				for (n = 4; n < 151 - mode.state_short_len; n++)
				{
					memVec[n] = 0;
				}

				System.arraycopy(residual, startPos, memVec, 151 - mode.state_short_len, mode.state_short_len);

				CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, residual, startPos + mode.state_short_len, memVec, 66, 85, diff, weightdenum, encoderBits.startIdx * 11, 0, 0, 0, cbSearchVariables, mode);
				CodingFunctions.cbConstruct(encoderBits, residual, startPos + mode.state_short_len, memVec, 66, (short) 85, diff, 0, 0, cbConstructVariables);
			}
			else
			{
				BasicFunctions.reverseCopy(dataVec, diff + 9, residual, encoderBits.startIdx * 40 - 40, diff);
				BasicFunctions.reverseCopy(memVec, 150, residual, startPos, mode.state_short_len);

				for (n = 4; n < 151 - mode.state_short_len; n++)
				{
					memVec[n] = 0;
				}

				CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, dataVec, 10, memVec, 66, 85, diff, weightdenum, (encoderBits.startIdx - 1) * 11, 0, 0, 0, cbSearchVariables, mode);
				CodingFunctions.cbConstruct(encoderBits, dataVec, 10, memVec, 66, (short) 85, diff, 0, 0, cbConstructVariables);

				BasicFunctions.reverseCopy(residual, startPos - 1, dataVec, 10, diff);
			}

			nFor = mode.subframes - encoderBits.startIdx - 1;
			subCount = 1;

			if (nFor > 0)
			{
				for (n = 4; n < 71; n++)
				{
					memVec[n] = 0;
				}

				System.arraycopy(residual, (encoderBits.startIdx - 1) * 40, memVec, 71, 80);

				for (subFrame = 0; subFrame < nFor; subFrame++)
				{
					CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, residual, (encoderBits.startIdx + 1 + subFrame) * 40, memVec, 4, 147, 40, weightdenum, (encoderBits.startIdx + 1 + subFrame) * 11, subCount, subCount * 3, subCount * 3, cbSearchVariables, mode);
					CodingFunctions.cbConstruct(encoderBits, residual, (encoderBits.startIdx + 1 + subFrame) * 40, memVec, 4, (short) 147, (short) 40, subCount * 3, subCount * 3, cbConstructVariables);

					temp = 4;
					for (n = 44; n < 151; n++)
					{
						memVec[temp++] = memVec[n];
					}

					System.arraycopy(residual, (encoderBits.startIdx + 1 + subFrame) * 40, memVec, 111, 40);
					subCount++;
				}
			}

			nBack = encoderBits.startIdx - 1;
			if (nBack > 0)
			{
				BasicFunctions.reverseCopy(dataVec, 10 + nBack * 40 - 1, residual, 0, nBack * 40);
				memlGotten = 40 * (mode.subframes + 1 - encoderBits.startIdx);
				if (memlGotten > 147)
				{
					memlGotten = 147;
				}

				BasicFunctions.reverseCopy(memVec, 150, residual, nBack * 40, memlGotten);
				for (n = 4; n < 151 - memlGotten; n++)
				{
					memVec[n] = 0;
				}

				for (subFrame = 0; subFrame < nBack; subFrame++)
				{
					CodingFunctions.cbSearch(encoderState, encoderBits, searchData, updateIndexData, dataVec, 10 + subFrame * 40, memVec, 4, 147, 40, weightdenum, (encoderBits.startIdx - 2 - subFrame) * 11, subCount, subCount * 3, subCount * 3, cbSearchVariables, mode);
					CodingFunctions.cbConstruct(encoderBits, dataVec, 10 + subFrame * 40, memVec, 4, (short) 147, (short) 40, subCount * 3, subCount * 3, cbConstructVariables);
					temp = 4;
					for (n = 44; n < 151; n++)
					{
						memVec[temp++] = memVec[n];
					}

					System.arraycopy(dataVec, 10 + subFrame * 40, memVec, 111, 40);
					subCount++;
				}

				BasicFunctions.reverseCopy(residual, 40 * nBack - 1, dataVec, 10, 40 * nBack);
			}

			short[] index = encoderBits.cbIndex;
			for (n = 4; n < 6; n++)
			{

				if (index[n] >= 108 && index[n] < 172)
				{
					index[n] -= 64;
				}
				else if (index[n] >= 236)
				{
					index[n] -= 128;
				}
				else
				{
					/* ERROR */
				}
			}
			CodingFunctions.packBits(encoderState, encoderBits, outData, packBitsVariables, mode);
		}
	}


	public final Decoder decoder;
	public final Encoder encoder;
	private byte[] encoderOut;
	public final Mode mode;

	public ILBC(Mode mode)
	{
		this.mode				= mode;
		this.encoder 			= new Encoder(mode);
		this.decoder 			= new Decoder(mode);
		this.encoderOut			= new byte[mode.bytes];

		encoder.reset();
		decoder.reset();

	}



	@Override
	public void decode(ByteBuffer input, ByteBuffer output)
	{
		if (input.remaining() != mode.bytes)
		{
			throw new IllegalStateException("packet must be prepare for szie = " + mode.bytes);
		}

		decoder.process(input,output);

	}

	@Override
	public void encode(ByteBuffer input, ByteBuffer output)
	{
		if(input.remaining() != mode.size * 2)
		{
			throw new IllegalStateException("packet must be prepare for szie = " + mode.size * 2);
		}

		encoder.process(input, encoderOut);

		output.clear();
		for (int i = 0; i < encoderOut.length; i++)
		{
			output.put(encoderOut[i]);
		}
	}



	@Override
	public int getCompressedSize()
	{
		return mode.bytes;
	}

}
