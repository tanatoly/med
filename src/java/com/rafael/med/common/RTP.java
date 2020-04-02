package com.rafael.med.common;

import java.nio.ByteBuffer;

public class RTP
{
	 public static final int RTP_PACKET_MAX_SIZE 	= 8192;
	 public static final int FIXED_HEADER_SIZE 		= 12;
	 public static final int EXT_HEADER_SIZE 		= 4; // The size of the extension header as defined by RFC 3550.
	 public static final int VERSION 				= 2; // Current supported RTP version
	 public static final int PCM_PACKAGE_SIZE		= 320;


	 /**
	  * Verion field.
	  *
	  * This field identifies the version of RTP. The version defined by
	  * this specification is two (2). (The value 1 is used by the first
	  * draft version of RTP and the value 0 is used by the protocol
	  * initially implemented in the "vat" audio tool.)
	  *
	  * @return the version value.
	  */
	 public static int getVersion(ByteBuffer buffer)
	 {
		 return (buffer.get(0) & 0xC0) >> 6;
	 }

	 /**
	  * Countributing source field.
	  *
	  * The CSRC list identifies the contributing sources for the
	  * payload contained in this packet. The number of identifiers is
	  * given by the CC field. If there are more than 15 contributing
	  * sources, only 15 may be identified. CSRC identifiers are inserted by
	  * mixers, using the SSRC identifiers of contributing
	  * sources. For example, for audio packets the SSRC identifiers of
	  * all sources that were mixed together to create a packet are
	  * listed, allowing correct talker indication at the receiver.
	  *
	  * @return The number of CSRC identifiers currently included in this packet.
	  */
	 public static int getCsrcCount(ByteBuffer buffer)
	 {
		 return buffer.get(0) & 0x0F;
	 }

	 /**
	  * Padding indicator.
	  *
	  * If the padding bit is set, the packet contains one or more
	  * additional padding octets at the end which are not part of the
	  * payload. The last octet of the padding contains a count of how
	  * many padding octets should be ignored. Padding may be needed by
	  * some encryption algorithms with fixed block sizes or for
	  * carrying several RTP packets in a lower-layer protocol data
	  * unit.
	  *
	  * @return true if padding bit set.
	  */
	 public static  boolean hasPadding(ByteBuffer buffer)
	 {
		 return (buffer.get(0) & 0x20) == 0x020;
	 }


	 /**
	  * Get RTP padding size from a RTP packet
	  *
	  * @return RTP padding size from source RTP packet
	  */
	 public int getPaddingSize(ByteBuffer buffer)
	 {
		 if ((buffer.get(0) & 0x4) == 0)
			 return 0;
		 else
			 return buffer.get(buffer.limit() - 1);
	 }



	 /**
	  * Extension indicator.
	  *
	  * If the extension bit is set, the fixed header is followed by
	  * exactly one header extension.
	  *
	  * @return true if extension bit set.
	  */
	 public static  boolean hasExtensions(ByteBuffer buffer)
	 {
		 return (buffer.get(0) & 0x10) == 0x010;
	 }

	 /**
	  * Marker bit.
	  *
	  * The interpretation of the marker is defined by a profile. It is
	  * intended to allow significant events such as frame boundaries to
	  * be marked in the packet stream. A profile may define additional
	  * marker bits or specify that there is no marker bit by changing
	  * the number of bits in the payload type field
	  *
	  * @return true if marker set.
	  */
	 public static  boolean getMarker(ByteBuffer buffer)
	 {
		 return (buffer.get(1) & 0xff & 0x80) == 0x80;
	 }

	 /**
	  * Payload type.
	  *
	  * This field identifies the format of the RTP payload and
	  * determines its interpretation by the application. A profile
	  * specifies a default static mapping of payload type codes to
	  * payload formats. Additional payload type codes may be defined
	  * dynamically through non-RTP means
	  *
	  * @return integer value of payload type.
	  */
	 public static  int getPayloadType(ByteBuffer buffer)
	 {
		 return (buffer.get(1) & 0xff & 0x7f);
	 }

	 /**
	  * Sequence number field.
	  *
	  * The sequence number increments by one for each RTP data packet
	  * sent, and may be used by the receiver to detect packet loss and
	  * to restore packet sequence. The initial value of the sequence
	  * number is random (unpredictable) to make known-plaintext attacks
	  * on encryption more difficult, even if the source itself does not
	  * encrypt, because the packets may flow through a translator that
	  * does.
	  *
	  * @return the sequence number value.
	  */
	 public static  int getSeqNumber(ByteBuffer buffer)
	 {
		 return buffer.getShort(2) & 0xFFFF;
	 }

	 /**
	  * Timestamp field.
	  *
	  * The timestamp reflects the sampling instant of the first octet
	  * in the RTP data packet. The sampling instant must be derived
	  * from a clock that increments monotonically and linearly in time
	  * to allow synchronization and jitter calculations.
	  * The resolution of the clock must be sufficient for the
	  * desired synchronization accuracy and for measuring packet
	  * arrival jitter (one tick per video frame is typically not
	  * sufficient).  The clock frequency is dependent on the format of
	  * data carried as payload and is specified statically in the
	  * profile or payload format specification that defines the format,
	  * or may be specified dynamically for payload formats defined
	  * through non-RTP means. If RTP packets are generated
	  * periodically, the nominal sampling instant as determined from
	  * the sampling clock is to be used, not a reading of the system
	  * clock. As an example, for fixed-rate audio the timestamp clock
	  * would likely increment by one for each sampling period.  If an
	  * audio application reads blocks covering 160 sampling periods
	  * from the input device, the timestamp would be increased by 160
	  * for each such block, regardless of whether the block is
	  * transmitted in a packet or dropped as silent.
	  *
	  * The initial value of the timestamp is random, as for the sequence
	  * number. Several consecutive RTP packets may have equal timestamps if
	  * they are (logically) generated at once, e.g., belong to the same
	  * video frame. Consecutive RTP packets may contain timestamps that are
	  * not monotonic if the data is not transmitted in the order it was
	  * sampled, as in the case of MPEG interpolated video frames. (The
	  * sequence numbers of the packets as transmitted will still be
	  * monotonic.)
	  *
	  * @return timestamp value
	  */
	 public static long getTimestamp(ByteBuffer buffer)
	 {
		 return ((long)(buffer.get(4) & 0xff) << 24) |
				 ((long)(buffer.get(5) & 0xff) << 16) |
				 ((long)(buffer.get(6) & 0xff) << 8)  |
				 ((long)(buffer.get(7) & 0xff));
	 }

	 /**
	  * Synchronization source field.
	  *
	  * The SSRC field identifies the synchronization source. This
	  * identifier is chosen randomly, with the intent that no two
	  * synchronization sources within the same RTP session will have
	  * the same SSRC identifier. Although the
	  * probability of multiple sources choosing the same identifier is
	  * low, all RTP implementations must be prepared to detect and
	  * resolve collisions.  Section 8 describes the probability of
	  * collision along with a mechanism for resolving collisions and
	  * detecting RTP-level forwarding loops based on the uniqueness of
	  * the SSRC identifier. If a source changes its source transport
	  * address, it must also choose a new SSRC identifier to avoid
	  * being interpreted as a looped source.
	  *
	  * @return the sysncronization source
	  */
	 public static long getSyncSource(ByteBuffer buffer)
	 {
		 return readUnsignedIntAsLong(buffer,8);
	 }

	 /**
	  * Get RTCP SSRC from a RTCP packet
	  *
	  * @return RTP SSRC from source RTP packet
	  */
	 public static  long GetRTCPSyncSource(ByteBuffer buffer)
	 {
		 return (readUnsignedIntAsLong(buffer,4));
	 }




	 /**
	  * Returns the length of the extensions currently added to this packet.
	  *
	  * @return the length of the extensions currently added to this packet.
	  */
	 public static int getExtensionLength(ByteBuffer buffer)
	 {
		 if (!hasExtensions(buffer))
			 return 0;

		 //the extension length comes after the RTP header, the CSRC list, and
		 //after two bytes in the extension header called "defined by profile"
		 int extLenIndex =  FIXED_HEADER_SIZE + getCsrcCount(buffer)  *4 + 2;
		 return ((buffer.get(extLenIndex) << 8) | buffer.get(extLenIndex + 1) * 4);
	 }

	 /**
	  * Get RTP header length from a RTP packet
	  *
	  * @return RTP header length from source RTP packet
	  */
	 public static int getHeaderLength(ByteBuffer buffer)
	 {
		 if(hasExtensions(buffer))
			 return FIXED_HEADER_SIZE + 4 * getCsrcCount(buffer) + EXT_HEADER_SIZE + getExtensionLength(buffer);
		 else
			 return FIXED_HEADER_SIZE + 4 * getCsrcCount(buffer);
	 }


	 /**
	  * Get RTP payload length from a RTP packet
	  *
	  * @return RTP payload length from source RTP packet
	  */
	 public static int getPayloadLength(ByteBuffer buffer)
	 {
		 return buffer.limit() - getHeaderLength(buffer);
	 }


	 public static int getCsrc(ByteBuffer buffer,byte[] dst)
	 {
		int csrcCount = getCsrcCount(buffer);
		int position = buffer.position();
		buffer.position(FIXED_HEADER_SIZE);
		buffer.get(dst,0, 4 * csrcCount);
		buffer.position(position);
		return  csrcCount;
	 }

	 /**
	  * Reads the data transported by RTP in a packet, for example
	  * audio samples or compressed video data.
	  *
	  * @param buff the buffer used for reading
	  * @param offset the initial offset inside buffer.
	  */
	 public static void getPayload(ByteBuffer buffer,byte[] buff, int offset)
	 {
		 int position = buffer.position();
		 buffer.position(FIXED_HEADER_SIZE);
		 buffer.get(buff, offset, buffer.limit() - FIXED_HEADER_SIZE);
		 buffer.position(position);
	 }

	 /**
	  * Read an unsigned integer as long at specified offset
	  *
	  * @param off start offset of this unsigned integer
	  * @return unsigned integer as long at offset
	  */
	 private static long readUnsignedIntAsLong(ByteBuffer buffer,int off)
	 {
		 buffer.position(off);
		 return (((long)(buffer.get() & 0xff) << 24) |
				 ((long)(buffer.get() & 0xff) << 16) |
				 ((long)(buffer.get() & 0xff) << 8) |
				 ((long)(buffer.get() & 0xff))) & 0xFFFFFFFFL;
	 }


	 public static void setExtensions(ByteBuffer buffer,boolean isExists)
	 {
		 byte first = buffer.get(0);
		 if(isExists)
		 {
			first  = (byte) (first | (1 << 4));
		 }
		 else
		 {
			 first  = (byte) (first & ~(1 << 4));
		 }
		 buffer.put(0,first);
	 }
}
