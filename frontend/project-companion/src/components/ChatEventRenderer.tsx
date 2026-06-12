import React, { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { 
  Lightbulb, 
  Database, 
  FileEdit,
  Loader2, 
  ChevronDown,
  ChevronRight
} from 'lucide-react';
import { ChatEvent, ChatEventType } from '@/lib/types';

export const ChatEventRenderer = ({ event, isLoading }: { event: ChatEvent, isLoading?: boolean }) => {
  switch (event.type) {
    case ChatEventType.THOUGHT:
      return (
        <div className="flex flex-col gap-1 text-[#949494] text-[13px] font-normal mb-4">
          <div className="flex items-center gap-2">
            {isLoading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Lightbulb className="w-4 h-4" />}
            <span className="font-semibold uppercase tracking-wider text-[11px] text-[#ececec]/60">Thought</span>
          </div>
          <div className="pl-6 border-l border-border/20 ml-2">
            {event.content}
          </div>
        </div>
      );

    case ChatEventType.TOOL_LOG:
      return <DetailedEvent 
                icon={<Database className="w-4 h-4" />} 
                type="Tool Log" 
                event={event} 
                isLoading={isLoading}
              />;

    case ChatEventType.FILE_EDIT:
      return <DetailedEvent 
                icon={isLoading ? <Loader2 className="w-4 h-4 animate-spin text-primary" /> : <FileEdit className="w-4 h-4" />} 
                type="File Edit" 
                event={event} 
                isLoading={isLoading}
              />;

    case ChatEventType.MESSAGE:
      return (
        <div className="bg-card/40 border border-border/40 shadow-sm rounded-xl p-4 mb-4">
          <div className="flex items-center gap-2 mb-2">
            <span className="font-semibold uppercase tracking-wider text-[11px] text-primary">Message</span>
          </div>
          <div className="prose prose-invert prose-sm max-w-none text-[#ececec] leading-relaxed">
            <ReactMarkdown remarkPlugins={[remarkGfm]}>
              {event.content}
            </ReactMarkdown>
            {isLoading && <span className="inline-block w-1.5 h-4 ml-1 bg-primary animate-pulse align-middle" />}
          </div>
        </div>
      );

    default:
      return null;
  }
};

const DetailedEvent = ({ 
  icon, 
  type, 
  event,
  isLoading
}: { 
  icon: React.ReactNode, 
  type: string, 
  event: ChatEvent,
  isLoading?: boolean
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  
  const pathData = event.filePath || event.metadata || event.metaData;

  return (
    <div className="flex flex-col gap-2 my-3 bg-base-200/30 rounded-lg p-3 border border-border/20">
      <div 
        className="flex items-center justify-between w-full cursor-pointer group"
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <div className="flex items-center gap-3">
          <div className="text-[#949494] shrink-0">{icon}</div>
          <div className="flex items-center gap-3 overflow-hidden">
            <span className="text-[#ececec]/80 text-[12px] font-semibold uppercase tracking-wider shrink-0">{type}</span>
            
            {pathData && (
              <span className="bg-[#262626] text-[#ececec] text-[12px] px-2 py-0.5 rounded-md font-mono border border-[#333] truncate">
                {pathData}
              </span>
            )}
            
            {isLoading && <span className="text-primary text-[11px] animate-pulse">Running...</span>}
          </div>
        </div>
        
        <div className="text-[#949494] group-hover:text-[#ececec] transition-colors">
          {isExpanded ? <ChevronDown className="w-4 h-4" /> : <ChevronRight className="w-4 h-4" />}
        </div>
      </div>

      {isExpanded && event.content && (
        <div className="mt-2 pl-7 animate-in fade-in slide-in-from-top-1 duration-200">
          <div className="bg-[#1a1a1a] border border-[#333] rounded-md p-3 overflow-x-auto text-[12px] font-mono text-[#ececec]">
            <pre className="whitespace-pre-wrap break-all">{event.content}</pre>
          </div>
        </div>
      )}
    </div>
  );
};