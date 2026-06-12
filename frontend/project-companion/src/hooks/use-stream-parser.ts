import { useMemo } from 'react';
import { ChatEvent, ChatEventType } from '@/lib/types';

// Regex to capture the three specific tags we support
// Matches: <tag attributes>content</tag>
// Note: This regex is designed to be lenient for streaming (doesn't require strict closing for the last item)
const PARSE_REGEX = /<(tool|message|file|thought)(?:[^>]*)>([\s\S]*?)(?:<\/\1>|$)/gi;
const ATTR_REGEX = /(?:path|args)="([^"]+)"/i;

export const useStreamParser = (streamBuffer: string) => {
  return useMemo(() => {
    const events: ChatEvent[] = [];
    let match: RegExpExecArray | [any, any, any];
    
    let lastIndex = 0;

    while ((match = PARSE_REGEX.exec(streamBuffer)) !== null) {
      // 1. Capture any text before the match as a MESSAGE event
      let textBefore = streamBuffer.substring(lastIndex, match.index).trim();
      textBefore = textBefore.replace(/^[\[\],]+|[\[\],]+$/g, '').trim();
      if (textBefore) {
        events.push({
          type: ChatEventType.MESSAGE,
          content: textBefore,
        });
      }

      const [fullMatch, tagName, content] = match;
      const typeStr = tagName.toLowerCase();
      
      // Extract attributes from the opening tag part of the match
      const openTagMatch = streamBuffer.substring(match.index, match.index + fullMatch.indexOf('>') + 1); 
      const attrMatch = ATTR_REGEX.exec(openTagMatch);
      const attrValue = attrMatch ? attrMatch[1] : undefined;

      let type: ChatEventType = ChatEventType.MESSAGE;
      let filePath: string | undefined;
      let metadata: string | undefined;

      if (typeStr === 'tool') {
        type = ChatEventType.TOOL_LOG;
        metadata = attrValue;
      } else if (typeStr === 'file') {
        type = ChatEventType.FILE_EDIT;
        filePath = attrValue;
      } else if (typeStr === 'thought') {
        type = ChatEventType.THOUGHT;
      }

      events.push({
        type,
        content: content.trim(),
        filePath,
        metadata
      });

      lastIndex = PARSE_REGEX.lastIndex;
    }

    // 2. Capture any remaining text after the last match
    let textAfter = streamBuffer.substring(lastIndex).trim();
    textAfter = textAfter.replace(/^[\[\],]+|[\[\],]+$/g, '').trim();
    if (textAfter) {
      events.push({
        type: ChatEventType.MESSAGE,
        content: textAfter,
      });
    }

    return events;
  }, [streamBuffer]);
};